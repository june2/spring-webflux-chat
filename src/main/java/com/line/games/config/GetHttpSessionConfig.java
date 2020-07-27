//package com.line.games.config;
//
//import com.line.games.messaging.RedisChatMessageListener;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
//import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
//
//import static com.line.games.config.ChatConstants.MESSAGE_COUNTER_KEY;
//
//
//@Slf4j
//public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator
//{
//	@Override
//	public void modifyHandshake(ServerEndpointConfig config,
//								HandshakeRequest request,
//								aHandshakeResponse response)
//	{
//		HttpSession httpSession = (HttpSession)request.getHttpSession();
//		config.getUserProperties().put(HttpSession.class.getName(),httpSession);
//	}
//}
