package com.ordersphere.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.factory.AddResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    @Bean
    public RouteLocator ordersphereRouteConfig(RouteLocatorBuilder builder){
        return builder.routes()
                .route(
                        p -> p.path("/ordersphere/users/**")
                                .filters(f -> f.rewritePath("/ordersphere/users/(?<segment>.*)", "/${segment}")
                                        .addResponseHeader("X-Response-Time",LocalDateTime.now().toString()))
                                .uri("lb://user-service")
                )
                .build();
    }
}
