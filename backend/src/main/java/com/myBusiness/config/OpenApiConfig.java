package com.myBusiness.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig configures the OpenAPI documentation for the application.
 * It provides API metadata that is used by the Swagger UI for interactive documentation.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates an OpenAPI bean containing metadata about the API.
     *
     * @return An OpenAPI instance with title, version, and description.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My Business Inventory Management API")
                        .version("1.0")
                        .description("This API manages inventory, users, authentication, and reporting for the My Business application."));
    }
}
