package com.example.reactor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author zhoutzzz
 */
@Configuration
public class RouteConfig {

    /**
     * webflux特性，纯reactor风格
     * @param postHandler
     * @return
     */
    @Bean
    public RouterFunction<ServerResponse> routes(TestService postHandler){
        return route(GET("/api/get"), postHandler::get)
                .andRoute(POST("post"), postHandler::post)
                .andRoute(PUT("put"), postHandler::put)
                .andRoute(DELETE("delete"), postHandler::delete);
    }

}
