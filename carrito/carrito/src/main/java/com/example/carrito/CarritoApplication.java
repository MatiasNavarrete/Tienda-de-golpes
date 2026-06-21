package com.example.carrito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		// Excluimos la auto-configuración conflictiva de Springdoc con HATEOAS en Spring Boot 4
		org.springdoc.core.configuration.SpringDocHateoasConfiguration.class
})
public class CarritoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarritoApplication.class, args);
	}

}
