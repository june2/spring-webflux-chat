package com.line.games.service;

import com.line.games.model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private Map<String, User> data;

    @PostConstruct
    public void init() {
        data = new HashMap<>();
        // test1@test.com:123456
        data.put("test1@test.com", new User("test1@test.com", "user1", "$2a$10$ton5ZTBcW5cGac1vsx4Gl.omOsZWoDpr2u52J7L0r70s9VY.mGK2W"));
        // test2@test.com:123456
        data.put("test2@test.com", new User("test2@test.com", "user2", "$2a$10$ton5ZTBcW5cGac1vsx4Gl.omOsZWoDpr2u52J7L0r70s9VY.mGK2W"));
    }

    public Mono<User> findByEmail(String email) {
        if (data.containsKey(email)) {
            return Mono.just(data.get(email));
        } else {
            return Mono.empty();
        }
    }

}