package com.line.games.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.line.games.model.ChatMessage;
import com.line.games.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.line.games.config.ChatConstants.MESSAGE_TOPIC;

@Component
@Slf4j
public class RedisChatMessagePublisher {

    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public Mono<Long> publishChatMessage(String room, ChatMessage chatMessage) {
        return Mono.fromCallable(() -> {
            String chatString = "";
            try {
                chatString = objectMapper.writeValueAsString(chatMessage);
            } catch (JsonProcessingException e) {
                log.error("Error converting ChatMessage {} into string", chatMessage, e);
            }
            return chatString;
        }).flatMap(chatString -> {
            log.info("chatString : {}", chatString);
            // Publish Message to Redis Channels
            return reactiveStringRedisTemplate.convertAndSend(MESSAGE_TOPIC, chatString)
                    .doOnSuccess(aLong -> log.debug("Message published to Redis Topic."))
                    .doOnError(throwable -> log.error("Error publishing message.", throwable));
        });
    }

}