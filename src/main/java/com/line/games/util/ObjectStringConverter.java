package com.line.games.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.line.games.model.ReciveMessage;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class ObjectStringConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ReciveMessage stringToObject(String data) {
        try {
            return objectMapper.readValue(data, ReciveMessage.class);
        } catch (Error | JsonProcessingException e) {
			return null;
        }
    }

    public static <T> Mono<T> stringToObject(String data, Class<T> clazz) {
        return Mono.fromCallable(() -> objectMapper.readValue(data, clazz))
                .doOnError(throwable -> log.error("Error converting [{}] to class '{}'.", data, clazz.getSimpleName()));
    }

    public static <T> Mono<String> objectToString(T object) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(object))
                .doOnError(throwable -> log.error("Error converting [{}] to String.", object));
    }

}
