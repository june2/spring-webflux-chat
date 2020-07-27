package com.line.games.handler;

import com.line.games.messaging.RedisChatMessagePublisher;
import com.line.games.model.ChatMessage;
import com.line.games.util.ObjectStringConverter;
import lombok.extern.slf4j.Slf4j;
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

    public ChatWebSocketHandler(DirectProcessor<ChatMessage> messageDirectProcessor,
                                RedisChatMessagePublisher redisChatMessagePublisher) {
        this.messageDirectProcessor = messageDirectProcessor;
        this.chatMessageFluxSink = messageDirectProcessor.sink();
        this.redisChatMessagePublisher = redisChatMessagePublisher;
    }

    @Override
        public Mono<Void> handle(WebSocketSession webSocketSession) {
        log.info("WebSocketSession :: [{}]", webSocketSession);
        String token = ObjectStringConverter.getToken(webSocketSession.getHandshakeInfo().getUri().toString());
        log.info("token :: [{}]", token);
        // Auth
        if(!token.equals("test")) {
            return webSocketSession.close();
        }
        Flux<WebSocketMessage> sendMessageFlux = messageDirectProcessor.flatMap(ObjectStringConverter::objectToString)
                .map(webSocketSession::textMessage)
                .doOnError(throwable -> log.error("Error Occurred while sending message to WebSocket.", throwable));
        Mono<Void> outputMessage = webSocketSession.send(sendMessageFlux);

        Mono<Void> inputMessage = webSocketSession.receive()
                .flatMap(webSocketMessage -> {
                    log.info("webSocketMessage: {}", webSocketMessage.getPayloadAsText());
                    return redisChatMessagePublisher.publishChatMessage(webSocketSession.getId(), webSocketMessage.getPayloadAsText());
                })
                .doOnSubscribe(subscription -> {
                    log.info("User '{}' Connected. Total Active Users: {}", webSocketSession, webSocketSession.getId());
                    chatMessageFluxSink.next(new ChatMessage(0, "CONNECTED", null));
                })
                .doOnError(throwable -> log.info("Error Occurred while sending message to Redis.", throwable))
                .doFinally(signalType -> {
                    log.info("User '{}' Disconnected. Total Active Users: {}", webSocketSession, webSocketSession.getId());
                    chatMessageFluxSink.next(new ChatMessage(0, "DISCONNECTED", null));
                })
                .then();

        return Mono.zip(inputMessage, outputMessage).then();
    }

    public Mono<Void> sendMessage(ChatMessage chatMessage) {
        return Mono.fromSupplier(() -> chatMessageFluxSink.next(chatMessage)).then();
    }

}
