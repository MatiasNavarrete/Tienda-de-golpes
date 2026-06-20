package com.example.busqueda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springdoc.core.configuration.SpringDocHateoasConfiguration.class
})
public class BusquedaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusquedaApplication.class, args);
	}

}
