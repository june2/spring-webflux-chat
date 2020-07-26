package com.line.games.handler;

import com.line.games.messaging.RedisChatMessageListener;
import com.line.games.messaging.RedisChatMessagePublisher;
import com.line.games.model.ChatMessage;
import com.line.games.model.ReciveMessage;
import com.line.games.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

    private final DirectProcessor<ChatMessage> messageDirectProcessor;
    private final FluxSink<ChatMessage> chatMessageFluxSink;
    private final RedisChatMessagePublisher redisChatMessagePublisher;
    private final RedisAtomicLong activeUserCounter;

    public ChatWebSocketHandler(DirectProcessor<ChatMessage> messageDirectProcessor,
                                RedisChatMessagePublisher redisChatMessagePublisher, RedisAtomicLong activeUserCounter) {
        this.messageDirectProcessor = messageDirectProcessor;
        this.chatMessageFluxSink = messageDirectProcessor.sink();
        this.redisChatMessagePublisher = redisChatMessagePublisher;
        this.activeUserCounter = activeUserCounter;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        log.info("WebSocketSession :: [{}]", webSocketSession);
        Flux<WebSocketMessage> sendMessageFlux = messageDirectProcessor.flatMap(ObjectStringConverter::objectToString)
                .map(webSocketSession::textMessage)
                .doOnError(throwable -> log.error("Error Occurred while sending message to WebSocket.", throwable));
        Mono<Void> outputMessage = webSocketSession.send(sendMessageFlux);

        Mono<Void> inputMessage = webSocketSession.receive()
                .flatMap(webSocketMessage -> {
                    log.info("webSocketMessage: {}", webSocketMessage.getPayloadAsText());
                    ReciveMessage msg = ObjectStringConverter.stringToObject(webSocketMessage.getPayloadAsText());
                    log.info("ReciveMessage: {}", msg);
                    return redisChatMessagePublisher.publishChatMessage(webSocketMessage.getPayloadAsText());
                })
                .doOnSubscribe(subscription -> {
                    long activeUserCount = activeUserCounter.incrementAndGet();
                    log.info("User '{}' Connected. Total Active Users: {}", webSocketSession, activeUserCount);
                    chatMessageFluxSink.next(new ChatMessage(0, "CONNECTED", activeUserCount));
                })
                .doOnError(throwable -> log.info("Error Occurred while sending message to Redis.", throwable))
                .doFinally(signalType -> {
                    long activeUserCount = activeUserCounter.decrementAndGet();
                    log.info("User '{}' Disconnected. Total Active Users: {}", webSocketSession.getId(), activeUserCount);
                    chatMessageFluxSink.next(new ChatMessage(0, "DISCONNECTED", activeUserCount));
                })
                .then();

        return Mono.zip(inputMessage, outputMessage).then();
    }

    public Mono<Void> sendMessage(ChatMessage chatMessage) {
        return Mono.fromSupplier(() -> chatMessageFluxSink.next(chatMessage)).then();
    }

}
