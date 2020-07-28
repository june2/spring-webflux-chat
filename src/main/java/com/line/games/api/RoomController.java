package com.line.games.api;

import com.line.games.messaging.RedisChatMessageListener;
import com.line.games.model.RoomRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class RoomController {

    @Autowired
    private RedisChatMessageListener redisChatMessageListener;

    @RequestMapping(value = "/api/room", method = RequestMethod.POST)
    public Mono<ResponseEntity<?>> create(@RequestBody RoomRequest req) {
        redisChatMessageListener.subscribeMessageChannelAndPublishOnWebSocket(req.getName())
                .doOnSubscribe(subscription -> log.info("Redis Listener Started"))
                .doOnError(throwable -> log.error("Error listening to Redis topic.", throwable))
                .doFinally(signalType -> log.info("Stopped Listener. Signal Type: {}", signalType))
                .subscribe();
        return Mono.just(ResponseEntity.ok(req.getName()));
    }

}
