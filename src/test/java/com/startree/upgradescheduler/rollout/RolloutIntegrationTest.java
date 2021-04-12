package com.startree.upgradescheduler.rollout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.startree.upgradescheduler.domain.ClusterPayload;
import com.startree.upgradescheduler.domain.ClusterStatePayload;
import com.startree.upgradescheduler.domain.UpgradePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.startree.upgradescheduler.domain.PatchType.FEATURE;
import static com.startree.upgradescheduler.entity.Status.COMPLETED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RolloutIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("rollout 2 out of 10")
    public void testRoll_two_percentage() throws Exception {

        //register 10 managed clusters on version 1.0.0
        for (int i = 1; i <= 10; i++) {

            ClusterPayload c = new ClusterPayload((long) i, "1.0.0", COMPLETED);

            this.mockMvc.perform(post("/v1/upgrade/clusters")
                    .contentType(APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(c)))
                    .andExpect(status().isCreated());
        }

        //add a new upgrade; version = 1.3.3.7
        UpgradePayload payload = new UpgradePayload("UNICORN", "1.3.3.7", "Super patch with lots of features", "", FEATURE);

        this.mockMvc.perform(post("/v1/upgrade")
                .contentType(APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(payload)))
                .andExpect(status().isCreated());

        //bind an upgrade with a rollout strategy
        ClusterStatePayload csPayload = new ClusterStatePayload(RolloutStrategy.PERCENTAGE, "2", "1.3.3.7");

        this.mockMvc.perform(post("/v1/scheduler/state")
                .contentType(APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(csPayload)))
                .andExpect(status().isCreated());


        //Get the new desired state; since the rollout strategy is based on clusterID and percentage
        //only the first 2 should get a new update
        for (int i = 1; i <= 10; i++) {
            if ( i <= 2) {
                this.mockMvc.perform(get("/v1/scheduler/state")
                        .queryParam("clusterId", "" + i))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.upgrade.version").value("1.3.3.7"));
            } else {
                this.mockMvc.perform(get("/v1/scheduler/state")
                        .queryParam("clusterId", "" + i))
                        .andExpect(status().isNotFound());
            }
        }
    }
}
