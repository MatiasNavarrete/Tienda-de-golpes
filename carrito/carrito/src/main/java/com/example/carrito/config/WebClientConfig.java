package com.example.carrito.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        //llamamos la ruta del servicio de productos
        return WebClient.create("http://localhost:8080");

    }
}