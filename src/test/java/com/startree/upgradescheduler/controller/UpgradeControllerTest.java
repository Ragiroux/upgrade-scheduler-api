package com.startree.upgradescheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.startree.upgradescheduler.domain.ClusterPayload;
import com.startree.upgradescheduler.domain.PatchType;
import com.startree.upgradescheduler.domain.UpgradePayload;
import com.startree.upgradescheduler.entity.Cluster;
import com.startree.upgradescheduler.entity.Upgrade;
import com.startree.upgradescheduler.repository.UpgradeRepository;
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

import java.time.LocalDate;
import java.util.Optional;

import static com.startree.upgradescheduler.domain.PatchType.FEATURE;
import static com.startree.upgradescheduler.entity.Status.COMPLETED;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
class UpgradeControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UpgradeRepository upgradeRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    void addNewUpgrade() throws Exception {
        UpgradePayload payload = new UpgradePayload("UNICORN", "1.3.3.7", "Super patch with lots of features", "", FEATURE);

        this.mockMvc.perform(post("/v1/upgrade")
                .contentType(APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andDo(document("post-upgrade",
                        responseFields(
                                fieldWithPath("id").description("Newly created id for the upgrade"),
                                fieldWithPath("upgradeName").description("The name of the pinot upgrade"),
                                fieldWithPath("version").description("New version"),
                                fieldWithPath("summary").description("Quick summary of the upgrade"),
                                fieldWithPath("patchUri").description("Uri for downloading patch (optional)"),
                                fieldWithPath("patchType").description("Patch type"))))
                .andExpect(jsonPath("$.upgradeName").value("UNICORN"))
                .andExpect(jsonPath("$.version").value("1.3.3.7"))
                .andExpect(jsonPath("$.patchType").value("FEATURE"));

        Optional<Upgrade> upgrade = upgradeRepository.findAll().stream().findFirst();

        assertNotNull(upgrade.get());
        assertEquals("UNICORN", upgrade.get().getUpgradeName());
    }
}