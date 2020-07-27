package com.line.games.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.line.games.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.line.games.config.ChatConstants.MESSAGE_TOPIC;

@Component
@Slf4j
public class RedisChatMessagePublisher {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final RedisAtomicInteger chatMessageCounter;
    private final ObjectMapper objectMapper;

    public RedisChatMessagePublisher(ReactiveStringRedisTemplate reactiveStringRedisTemplate, RedisAtomicInteger chatMessageCounter, ObjectMapper objectMapper) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.chatMessageCounter = chatMessageCounter;
        this.objectMapper = objectMapper;
    }

    public Mono<Long> publishChatMessage(String id, String message) {
        Integer totalChatMessage = chatMessageCounter.incrementAndGet();
        return Mono.fromCallable(() -> {
            ChatMessage chatMessage = new ChatMessage(totalChatMessage, message, id);
            String chatString = "EMPTY_MESSAGE";
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
                    .doOnSuccess(aLong -> log.info("Message published to Redis Topic."))
                    .doOnError(throwable -> log.error("Error publishing message.", throwable));
        });
    }
}
