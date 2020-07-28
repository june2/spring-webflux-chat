package com.line.games.api;

import com.line.games.messaging.RedisChatMessageListener;
import com.line.games.model.RoomRequest;
import com.line.games.service.RoomService;
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
    @Autowired
    private RoomService roomService;

    @RequestMapping(value = "/api/room", method = RequestMethod.POST)
    public Mono<ResponseEntity<?>> create(@RequestBody RoomRequest req) {
        return roomService.create(req.getName()).map(room -> {
            redisChatMessageListener.subscribeMessageChannelAndPublishOnWebSocket(room.getName())
                    .doOnSubscribe(subscription -> log.debug("{} room created", room.getName()))
                    .doOnError(throwable -> log.error("Error listening to Redis topic.", throwable))
                    .doFinally(signalType -> log.debug("Stopped Listener. Signal Type: {}", signalType))
                    .subscribe();
            return ResponseEntity.ok(room);
        });
    }

    @RequestMapping(value = "/api/rooms", method = RequestMethod.GET)
    public Mono<ResponseEntity<?>> findAll() {
        return Mono.just(ResponseEntity.ok(roomService.findAll()));
    }
}
