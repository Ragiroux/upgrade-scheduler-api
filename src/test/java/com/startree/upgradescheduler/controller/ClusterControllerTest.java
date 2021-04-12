package com.startree.upgradescheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.startree.upgradescheduler.domain.ClusterPayload;
import com.startree.upgradescheduler.entity.Cluster;
import com.startree.upgradescheduler.repository.ClusterRepository;
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

import static com.startree.upgradescheduler.entity.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
class ClusterControllerTest {

    private MockMvc mockMvc;

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
    void registerClusterToScheduler() throws Exception {

        long clusterId = new Random().nextInt(1000);

        ClusterPayload c = new ClusterPayload(clusterId, "1.0.0", COMPLETED);

        this.mockMvc.perform(post("/v1/upgrade/clusters")
                .contentType(APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(c)))
                .andExpect(status().isCreated())
                .andDo(document("post-cluster",
                        responseFields(
                                fieldWithPath("clusterId").description("The pinot cluster id"),
                                fieldWithPath("version").description("Current managed cluster version"),
                                fieldWithPath("status").description("current managed cluster status"))))
                .andExpect(jsonPath("$.clusterId").value(clusterId))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        Optional<Cluster> cluster = clusterRepository.findByClusterId(clusterId);

        assertNotNull(cluster.get());
    }

    @Test
    void updateClusterInformation() throws Exception {

        long clusterId = new Random().nextInt(1000);

        clusterRepository.save(Cluster.builder().clusterId(clusterId).version("1.0.0").status("COMPLETED").build());

        ClusterPayload updatedClusterPayload = new ClusterPayload(clusterId, "1.0.1", UPGRADING);
        this.mockMvc.perform(put("/v1/upgrade/clusters/{clusterId}", clusterId)
                .contentType(APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(updatedClusterPayload)))
                .andExpect(status().isOk())
                .andDo(document("put-cluster",
                        responseFields(
                                fieldWithPath("clusterId").description("The pinot cluster id"),
                                fieldWithPath("version").description("Current managed cluster version"),
                                fieldWithPath("status").description("current managed cluster status"))))
                //THEN
                .andExpect(jsonPath("$.clusterId").value(clusterId))
                .andExpect(jsonPath("$.version").value("1.0.1"))
                .andExpect(jsonPath("$.status").value("UPGRADING"));

        Optional<Cluster> cluster = clusterRepository.findByClusterId(clusterId);
        assertNotNull(cluster.get());
        assertEquals("1.0.1", cluster.get().getVersion());
    }
}