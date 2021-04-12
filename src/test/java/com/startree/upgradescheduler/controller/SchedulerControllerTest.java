package com.startree.upgradescheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.startree.upgradescheduler.domain.ClusterStatePayload;
import com.startree.upgradescheduler.entity.Cluster;
import com.startree.upgradescheduler.entity.ClusterState;
import com.startree.upgradescheduler.entity.Upgrade;
import com.startree.upgradescheduler.repository.ClusterRepository;
import com.startree.upgradescheduler.repository.ClusterStateRepository;
import com.startree.upgradescheduler.repository.UpgradeRepository;
import com.startree.upgradescheduler.rollout.RolloutStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
class SchedulerControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UpgradeRepository upgradeRepository;

    @Autowired
    private ClusterStateRepository clusterStateRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    public void getState() throws Exception {

        String clusterId = UUID.randomUUID().toString();

        //add a managed cluster to the upgrade-scheduler
        clusterRepository.save(Cluster.builder().clusterId(clusterId).version("1.0.0").status("COMPLETED").build());
        //add an upgrade
        Upgrade upgradeSaved = upgradeRepository.save(Upgrade.builder()
                .upgradeName("CHRISTMAS ELF")
                .version("1.0.1")
                .patchType("FEATURE")
                .build());
        //add a rollout strategy to the upgrade
        clusterStateRepository.save(ClusterState.builder().rolloutStrategy("PERCENTAGE").rolloutParameter("20").currentUpgradeId(upgradeSaved.getId()).build());

        this.mockMvc.perform(get("/v1/scheduler/state")
                .queryParam("clusterId", "" + clusterId))
                .andExpect(status().isOk())
                .andDo(document("get-state",
                        responseFields(
                                fieldWithPath("clusterId").description("The pinot cluster id"),
                                fieldWithPath("updateAt").description("Time at which the update is scheduled"),
                                fieldWithPath("upgrade.id").description("upgrade id"),
                                fieldWithPath("upgrade.upgradeName").description("upgrade name"),
                                fieldWithPath("upgrade.version").description("the new version to be upgraded at"),
                                fieldWithPath("upgrade.summary").description("Summary of the upgrade"),
                                fieldWithPath("upgrade.patchUri").description("upgrade uri (optional)"),
                                fieldWithPath("upgrade.patchType").description("type of patch, Feature, Bugfix, Security"),
                                fieldWithPath("nextCallBack").description("Time at which the pinot cluster should call for a new state"))))
                .andExpect(jsonPath("$.clusterId").value(clusterId))
                .andExpect(jsonPath("$.upgrade.version").value("1.0.1"));
    }

    @Test
    public void addNewDesiredState() throws Exception {

        //add new upgrade into the system
        Upgrade upgradeSaved = upgradeRepository.save(Upgrade.builder()
                .upgradeName("UNICORN")
                .version("1.3.3.7")
                .patchType("FEATURE")
                .build());

        ClusterStatePayload csPayload = new ClusterStatePayload(RolloutStrategy.PERCENTAGE, "20", "1.3.3.7");

        this.mockMvc.perform(post("/v1/scheduler/state")
                .contentType(APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(csPayload)))
                .andExpect(status().isCreated())
                .andDo(document("post-cluster-state",
                        responseFields(
                                fieldWithPath("rolloutStrategy").description("The canary release strategy to use for the new version"),
                                fieldWithPath("rolloutParameter").description("The strategy parameter i.e percentage 20%"),
                                fieldWithPath("version").description("New version to rollout to managed cluster"))))
                .andExpect(jsonPath("$.rolloutStrategy").value("PERCENTAGE"))
                .andExpect(jsonPath("$.version").value("1.3.3.7"))
                .andExpect(jsonPath("$.rolloutParameter").value("20"));

        Optional<ClusterState> latestState = clusterStateRepository.findFirstByOrderByIdDesc();
        Optional<Upgrade> upgrade = upgradeRepository.findById(latestState.get().getCurrentUpgradeId());

        assertEquals(upgradeSaved.getId(), latestState.get().getCurrentUpgradeId());
        assertEquals("PERCENTAGE", latestState.get().getRolloutStrategy());
        assertEquals("1.3.3.7", upgrade.get().getVersion());
    }
}