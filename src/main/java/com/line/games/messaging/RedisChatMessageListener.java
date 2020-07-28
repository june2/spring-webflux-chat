package com.line.games.messaging;

import com.line.games.handler.ChatWebSocketHandler;
import com.line.games.model.ChatMessage;
import com.line.games.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.line.games.config.ChatConstants.MESSAGE_TOPIC;

@Component
@Slf4j
public class RedisChatMessageListener {

    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final ChatWebSocketHandler chatWebSocketHandler;

    public RedisChatMessageListener(ReactiveStringRedisTemplate reactiveStringRedisTemplate, ChatWebSocketHandler chatWebSocketHandler) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    /**
     * redis listener 생성
     */
    public Mono<Void> subscribeMessageChannelAndPublishOnWebSocket(String room) {
        return reactiveStringRedisTemplate.listenToChannel(MESSAGE_TOPIC + room)
                .map(ReactiveSubscription.Message::getMessage)
                .flatMap(message -> ObjectStringConverter.stringToObject(message, ChatMessage.class))
                .filter(chatMessage -> !chatMessage.getMessage().isEmpty())
                .flatMap(chatWebSocketHandler::sendMessage)
                .then();
    }

}
