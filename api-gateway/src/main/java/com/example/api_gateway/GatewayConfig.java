package com.example.api_gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        RouterFunction<ServerResponse> usuariosRoute = route("usuario")
                .route(path("/api/usuarios/**"), http())
                .before(uri("http://usuarios:8081")) // La URL ahora va aquí
                .build();

        RouterFunction<ServerResponse> busquedaRoute = route("busqueda")
                .route(path("/api/busqueda/**"), http())
                .before(uri("http://busqueda:8082"))
                .build();

        RouterFunction<ServerResponse> notificacionesRoute = route("notificaciones")
                .route(path("/api/notificaciones/**"), http())
                .before(uri("http://notificaciones:8083"))
                .build();

        return usuariosRoute.and(busquedaRoute).and(notificacionesRoute);
    }
}