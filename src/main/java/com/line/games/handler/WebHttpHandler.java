package com.line.games.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration(proxyBeanMethods = false)
public class WebHttpHandler {

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/static/index.html") Resource html,
                                                     @Value("classpath:/static/login.html") Resource login) {
        return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html))
                .andRoute(GET("/login"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(login));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Message {
        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Room {
        private String name;
    }

}
