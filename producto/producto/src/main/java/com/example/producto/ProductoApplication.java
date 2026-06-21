package com.example.producto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		// Excluimos la auto-configuración conflictiva de Springdoc con HATEOAS en Spring Boot 4
		org.springdoc.core.configuration.SpringDocHateoasConfiguration.class
})
public class ProductoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductoApplication.class, args);
	}

}