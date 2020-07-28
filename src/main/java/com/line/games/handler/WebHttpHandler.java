package com.line.games.handler;

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

    /**
     * static page router
     * TODO: 서버에서 인증처리하여 페이지 리다이렉트 로직 구현 필요
     *       현재는 프론트화면에서만 체크하여 리다이렉트하는 중
     */
    @Bean
    public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/static/index.html") Resource html,
                                                     @Value("classpath:/static/login.html") Resource login) {
        return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html))
                .andRoute(GET("/login"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(login));
    }
}
