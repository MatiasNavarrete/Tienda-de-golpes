package com.example.envio.config;

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
                        .title("API de Pedidos - Tienda de Golpes")
                        .version("1.0")
                        .description("Microservicio encargado de la gestión, creación y trazabilidad de pedidos.")
                        .contact(new Contact()
                                .name("alexander campos")
                                .email("ales@ejemplo.cl")));
    }
}