package com.line.games.messaging;

import com.line.games.handler.ChatWebSocketHandler;
import com.line.games.model.ChatMessage;
import com.line.games.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.line.games.config.ChatConstants.MESSAGE_TOPIC;

@Component
@Slf4j
public class RedisChatMessageListener {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    public RedisChatMessageListener(ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
    }

    /**
     * redis listener 생성
     * TODO: 멀티채널 변경필요!
     */
    public Mono<Void> subscribeMessageChannelAndPublishOnWebSocket(String name) {
        return reactiveStringRedisTemplate.listenTo(new PatternTopic(MESSAGE_TOPIC + name))
                .map(ReactiveSubscription.Message::getMessage)
                .flatMap(message -> ObjectStringConverter.stringToObject(message, ChatMessage.class))
                .filter(chatMessage -> !chatMessage.getMessage().isEmpty())
                .then();
    }

}
