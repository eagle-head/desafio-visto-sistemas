package br.com.productmanagementsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Management System API")
                        .description("RESTful API for product management operations including CRUD functionality")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Product Management System")
                                .email("contact@example.com")
                                .url("https://example.com"))
                );
    }
}