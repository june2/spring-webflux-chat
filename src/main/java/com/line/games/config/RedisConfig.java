package com.line.games.config;

import com.line.games.messaging.RedisChatMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Slf4j
@Configuration(proxyBeanMethods=false)
public class RedisConfig {

	/**
	 * redis connection
	 */
	@Bean
    ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisProperties redisProperties) {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
		redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	/**
	 * redis listener 생성
	 * TODO: 멀티채널 변경필요!
	 */
	@Bean
    ApplicationRunner applicationRunner(RedisChatMessageListener redisChatMessageListener) {
		return args -> {
			redisChatMessageListener.subscribeMessageChannelAndPublishOnWebSocket()
				.doOnSubscribe(subscription -> log.info("Redis Listener Started"))
				.doOnError(throwable -> log.error("Error listening to Redis topic.", throwable))
				.doFinally(signalType -> log.info("Stopped Listener. Signal Type: {}", signalType))
				.subscribe();
		};
	}

}
