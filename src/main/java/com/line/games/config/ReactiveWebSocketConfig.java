package com.line.games.config;

import com.line.games.handler.ChatWebSocketHandler;
import com.line.games.messaging.RedisChatMessagePublisher;
import com.line.games.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.DirectProcessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.line.games.config.ChatConstants.WEBSOCKET_MESSAGE_MAPPING;

@Slf4j
@Configuration(proxyBeanMethods=false)
public class ReactiveWebSocketConfig {

	@Bean
	public ChatWebSocketHandler webSocketHandler(RedisChatMessagePublisher redisChatMessagePublisher, RedisAtomicLong activeUserCounter) {
		DirectProcessor<ChatMessage> messageDirectProcessor = DirectProcessor.create();
		return new ChatWebSocketHandler(messageDirectProcessor, redisChatMessagePublisher, activeUserCounter);
	}

	@Bean
	public HandlerMapping webSocketHandlerMapping(ChatWebSocketHandler webSocketHandler) {
		Map<String, WebSocketHandler> map = new HashMap<>();
		map.put(WEBSOCKET_MESSAGE_MAPPING, webSocketHandler);
		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setCorsConfigurations(Collections.singletonMap("*", new CorsConfiguration().applyPermitDefaultValues()));
		handlerMapping.setOrder(1);
		handlerMapping.setUrlMap(map);
		return handlerMapping;
	}

	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

}
