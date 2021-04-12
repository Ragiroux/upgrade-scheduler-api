package com.startree.upgradescheduler.rollout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.startree.upgradescheduler.domain.ClusterPayload;
import com.startree.upgradescheduler.domain.ClusterStatePayload;
import com.startree.upgradescheduler.domain.UpgradePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static com.startree.upgradescheduler.domain.PatchType.FEATURE;
import static com.startree.upgradescheduler.entity.Status.COMPLETED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("rollout 0% with 100 clusters")
    public void testRoll_0_percentage_with_100_clusters() throws Exception {

        int maxClusters = 100;
        String rolloutPercentage = "0";
        Map<Integer, Integer> results = new HashMap<>();

        //register managed clusters on version 1.0.0
        List<String> clusterIds = registerClusterToScheduler(maxClusters);

        //add a new upgrade; version = 1.3.3.7
        addNewUpgrade();

        //bind an upgrade with a rollout strategy
        defineNewClusterState(rolloutPercentage);


        //Get the new desired state; since the rollout strategy is based on cluster database generated ID and percentage
        //only the first 2 should get a new update
        getNewStateForEachCluster(results, clusterIds);

        assertNull(results.get(200));
        assertEquals(100, results.get(404));
    }

    @Test
    @DisplayName("rollout 20% with 100 clusters")
    public void testRoll_20_percentage_with_100_clusters() throws Exception {

        int maxClusters = 100;
        String rolloutPercentage = "20";
        Map<Integer, Integer> results = new HashMap<>();
        List<String> clusterIds = registerClusterToScheduler(maxClusters);
        addNewUpgrade();

        //bind an upgrade with a rollout strategy
        defineNewClusterState(rolloutPercentage);


        //Get the new desired state; since the rollout strategy is based on cluster database generated ID and percentage
        //only the first 2 should get a new update
        getNewStateForEachCluster(results, clusterIds);
        assertEquals(20, results.get(200));
        assertEquals(80, results.get(404));
    }

    @Test
    @DisplayName("rollout 90% with 100 clusters")
    public void testRoll_90_percentage_with_100_clusters() throws Exception {

        int maxClusters = 100;
        String rolloutPercentage = "90";
        Map<Integer, Integer> results = new HashMap<>();

        //register managed clusters on version 1.0.0
        List<String> clusterIds = registerClusterToScheduler(maxClusters);

        //add a new upgrade; version = 1.3.3.7
        addNewUpgrade();

        //bind an upgrade with a rollout strategy
        defineNewClusterState(rolloutPercentage);


        //Get the new desired state; since the rollout strategy is based on cluster database generated ID and percentage
        //only the first 2 should get a new update
        getNewStateForEachCluster(results, clusterIds);

        assertEquals(90, results.get(200));
        assertEquals(10, results.get(404));
    }

    @Test
    @DisplayName("rollout 100% with 100 clusters")
    public void testRoll_100_percentage_with_100_clusters() throws Exception {

        int maxClusters = 100;
        String rolloutPercentage = "100";
        Map<Integer, Integer> results = new HashMap<>();

        //register managed clusters on version 1.0.0
        List<String> clusterIds = registerClusterToScheduler(maxClusters);

        //add a new upgrade; version = 1.3.3.7
        addNewUpgrade();

        //bind an upgrade with a rollout strategy
        defineNewClusterState(rolloutPercentage);


        //Get the new desired state; since the rollout strategy is based on cluster database generated ID and percentage
        //only the first 2 should get a new update
        getNewStateForEachCluster(results, clusterIds);

        assertEquals(100, results.get(200));
        assertNull(results.get(404));
    }

    @Test
    @DisplayName("rollout 20% with 1000 clusters")
    public void testRoll_20_percentage_with_1000_clusters() throws Exception {

        int maxClusters = 1000;
        String rolloutPercentage = "20";
        Map<Integer, Integer> results = new HashMap<>();

        //register managed clusters on version 1.0.0
        List<String> clusterIds = registerClusterToScheduler(maxClusters);

        //add a new upgrade; version = 1.3.3.7
        addNewUpgrade();

        defineNewClusterState(rolloutPercentage);

        getNewStateForEachCluster(results, clusterIds);

        assertEquals(200, results.get(200));
        assertEquals(800, results.get(404));
    }

    private void addNewUpgrade() throws Exception {
        //add a new upgrade; version = 1.3.3.7
        UpgradePayload payload = new UpgradePayload("UNICORN", "1.3.3.7", "Super patch with lots of features", "", FEATURE);

        this.mockMvc.perform(post("/v1/upgrade")
                .contentType(APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    private List<String> registerClusterToScheduler(int maxClusters) throws Exception {
        //register 100 managed clusters on version 1.0.0
        List<String> clusterIds = new ArrayList<>();
        for (int i = 0; i < maxClusters; i++) {
            String clusterId = UUID.randomUUID().toString();
            clusterIds.add(clusterId);
            ClusterPayload c = new ClusterPayload(clusterId, "1.0.0", COMPLETED);

            this.mockMvc.perform(post("/v1/clusters")
                    .contentType(APPLICATION_JSON_VALUE)
                    .content(new ObjectMapper().writeValueAsString(c)))
                    .andExpect(status().isCreated());
        }
        return clusterIds;
    }

    private void getNewStateForEachCluster(Map<Integer, Integer> results, List<String> clusterIds) throws Exception {
        for (String clusterId : clusterIds) {

            MvcResult mvcResult = this.mockMvc.perform(get("/v1/scheduler/state")
                    .queryParam("clusterId", "" + clusterId)).andReturn();

            int status = mvcResult.getResponse().getStatus();

            if (!results.containsKey(status)) {
                results.put(status, 1);
            } else {
                results.put(status, results.get(status) + 1);
            }
        }
    }

    private void defineNewClusterState(String rolloutPercentage) throws Exception {
        ClusterStatePayload csPayload = new ClusterStatePayload(RolloutStrategy.PERCENTAGE, rolloutPercentage, "1.3.3.7");

        this.mockMvc.perform(post("/v1/scheduler/state")
                .contentType(APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(csPayload)))
                .andExpect(status().isCreated());
    }
}
