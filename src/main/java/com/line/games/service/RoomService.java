package com.line.games.service;

import com.line.games.model.Room;
import com.line.games.model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoomService {

    /**
     * mongoDb로 변경 필요!
     */
    private Map<String, Room> data;

    @PostConstruct
    public void init() {
        data = new HashMap<>();
    }

    /**
     * 방 리스트 조회
     */
    public Mono<Map<String, Room>> findAll() {
        return Mono.just(data);
    }

    /**
     * 방 생성
     */
    public Mono<Room> create(String name) {
        Room room;
        if (data.containsKey(name)) {
            room = data.get(name);
        } else {
            room = Room.builder().id(data.size() + 1).name(name).build();
            data.put(name, room);
        }
        return Mono.just(room);
    }

}