package com.line.games.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.line.games.messaging.RedisChatMessagePublisher;
import com.line.games.model.ChatMessage;
import com.line.games.model.Type;
import com.line.games.model.User;
import com.line.games.service.JwtService;
import com.line.games.util.ObjectStringConverter;
import com.line.games.util.Security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

    private final DirectProcessor<ChatMessage> messageDirectProcessor;
    private final FluxSink<ChatMessage> chatMessageFluxSink;
    private final RedisChatMessagePublisher redisChatMessagePublisher;
    private final JwtService jwtService;
    private ObjectMapper mapper;

    public ChatWebSocketHandler(DirectProcessor<ChatMessage> messageDirectProcessor,
                                RedisChatMessagePublisher redisChatMessagePublisher, JwtService jwtService) {
        this.messageDirectProcessor = messageDirectProcessor;
        this.chatMessageFluxSink = messageDirectProcessor.sink();
        this.redisChatMessagePublisher = redisChatMessagePublisher;
        this.mapper = new ObjectMapper();
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        // Auth
        String token = Security.getToken(webSocketSession.getHandshakeInfo().getUri().toString());
        log.info("token :: [{}]", token);
        User user = jwtService.verify(token);
        if (!Optional.ofNullable(user).isPresent()) {
            return webSocketSession.close();
        }
        Flux<WebSocketMessage> sendMessageFlux = messageDirectProcessor.flatMap(ObjectStringConverter::objectToString)
                .map(webSocketSession::textMessage)
                .doOnError(throwable -> log.error("Error Occurred while sending message to WebSocket.", throwable));
        Mono<Void> outputMessage = webSocketSession.send(sendMessageFlux);

        Mono<Void> inputMessage = webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::toChatMessage)
                .doOnNext(chatMessage -> {
                    chatMessage.setUserId(user.getId());
                    chatMessage.setType(Type.CHAT_MESSAGE);
                    log.info("chatMessage : {}", chatMessage.toString());
//                    sendMessage(chatMessage);
                    redisChatMessagePublisher.publishChatMessage("", chatMessage);
                })
                .doOnSubscribe(subscription -> {
                    log.info("User '{}' Connected.", user.toString(), webSocketSession);
                    chatMessageFluxSink.next(new ChatMessage(Type.USER_JOINED, user.getEmail(), user.getId()));
                })
                .doOnError(throwable -> log.info("Error Occurred while sending message to Redis.", throwable))
                .doFinally(signalType -> {
                    log.info("User '{}' Disconnected.", webSocketSession);
                    chatMessageFluxSink.next(new ChatMessage(Type.USER_LEFT, user.getEmail(), user.getId()));
                })
                .then();

        return Mono.zip(inputMessage, outputMessage).then();
    }

    /**
     * convert json to message
     */
    private ChatMessage toChatMessage(String json) {
        try {
            return mapper.readValue(json, ChatMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON:" + json, e);
        }
    }

    /**
     * send message
     */
    private void sendMessage(ChatMessage chatMessage) {
        chatMessageFluxSink.next(chatMessage);
    }
}
