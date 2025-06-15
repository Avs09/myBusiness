package com.myBusiness.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  
class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void listAllProductsShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"));
    }

    @Test
    void getProductByIdShouldReturnOk() throws Exception {
        // Primero, crea un producto de prueba
        String json = "{\"name\":\"TestProduct\",\"thresholdMin\":1,\"thresholdMax\":10,\"price\":5.0,\"categoryId\":1,\"unitId\":1}";
        String location = mockMvc.perform(post("/api/products")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("TestProduct"));
    }

    @Test
    void createProductShouldReturnBadRequestForInvalidInput() throws Exception {
        String invalidJson = "{\"name\":\"\",\"thresholdMin\":-1}";
        mockMvc.perform(post("/api/products")
                .contentType("application/json")
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateProductShouldReturnOk() throws Exception {
        // Crear producto
        String json = "{\"name\":\"OldName\",\"thresholdMin\":1,\"thresholdMax\":10,\"price\":5.0,\"categoryId\":1,\"unitId\":1}";
        String location = mockMvc.perform(post("/api/products")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getHeader("Location");

        // Actualizar
        String updatedJson = "{\"name\":\"NewName\",\"thresholdMin\":2,\"thresholdMax\":20,\"price\":10.0,\"categoryId\":1,\"unitId\":1}";
        mockMvc.perform(put(location)
                .contentType("application/json")
                .content(updatedJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("NewName"));
    }

    @Test
    void deleteProductShouldReturnNoContent() throws Exception {
        // Crear producto
        String json = "{\"name\":\"ToDelete\",\"thresholdMin\":1,\"thresholdMax\":10,\"price\":5.0,\"categoryId\":1,\"unitId\":1}";
        String location = mockMvc.perform(post("/api/products")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(delete(location))
            .andExpect(status().isNoContent());

        mockMvc.perform(get(location))
            .andExpect(status().isNotFound());
    }

    @Test
    void listPaginatedShouldReturnCorrectPage() throws Exception {
        // Assuming at least 5 products exist
        mockMvc.perform(get("/api/products?page=0&size=2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    void getNonexistentProductShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
            .andExpect(status().isNotFound());
    }
}
