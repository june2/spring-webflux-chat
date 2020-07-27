package com.line.games.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@ToString
public class ChatMessage {
    private static AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final int id;
    private Type type;
    private String message;
    private Long userId;
    private final long timestamp;

    @JsonCreator
    public ChatMessage(
            @NonNull @JsonProperty("type") Type type,
            @NonNull @JsonProperty("message") String message,
            @JsonProperty("userId") Long userId) {
        this.id = ID_GENERATOR.addAndGet(1);
        this.type = type;
        this.message = message;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }
}
