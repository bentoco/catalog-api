package com.bentoco.catalog.dynamodb.adapters;


import com.bentoco.catalog.configs.AwsConfig;
import com.bentoco.catalog.configs.CategoriesDbConfig;
import com.bentoco.catalog.configs.TestcontainersConfig;
import com.bentoco.catalog.controller.CategoryController;
import com.bentoco.catalog.controller.request.CreateCategoryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import({TestcontainersConfig.class, AwsConfig.class, CategoriesDbConfig.class, CategoryController.class})
public class CategoryPersistenceTest {


    @Test
    public void shouldCreateCategory(@Autowired MockMvc mockMvc) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateCategoryRequest(
                                "beers and drinks",
                                "quench your thirst with our curated selection. cheers!"
                        ))))
                .andExpect(status().isCreated());
    }
}