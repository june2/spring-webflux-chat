package com.line.games.service;

import com.line.games.model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    /**
     * mongoDb로 변경 필요!
     * 현재는 임시로 map에 user 데이터를 저장하여 관리한다.
     */
    private Map<String, User> data;

    @PostConstruct
    public void init() {
        data = new HashMap<>();
        // test1@test.com:123456
        data.put("test1@test.com", new User(1L, "test1@test.com", "user1", "$2a$10$ton5ZTBcW5cGac1vsx4Gl.omOsZWoDpr2u52J7L0r70s9VY.mGK2W"));
        // test2@test.com:123456
        data.put("test2@test.com", new User(2L, "test2@test.com", "user2", "$2a$10$ton5ZTBcW5cGac1vsx4Gl.omOsZWoDpr2u52J7L0r70s9VY.mGK2W"));
        // test3@test.com:123456
        data.put("test3@test.com", new User(3L, "test3@test.com", "user3", "$2a$10$ton5ZTBcW5cGac1vsx4Gl.omOsZWoDpr2u52J7L0r70s9VY.mGK2W"));
    }

    /**
     * 이메일 주소로 유저 찾기
     */
    public Mono<User> findByEmail(String email) {
        if (data.containsKey(email)) {
            return Mono.just(data.get(email));
        } else {
            return Mono.empty();
        }
    }

}