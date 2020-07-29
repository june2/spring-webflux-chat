## spring-boot-webflux-ws

### This app include the following features:
- Java8
- Spring-boot
- Gradle
- webflux
- redis
- websocket

### Demo 
![Jul-29-2020 11-11-37](https://user-images.githubusercontent.com/5827617/88748645-7cf0bd80-d18c-11ea-9cdb-91623172c607.gif)


### Redis 
```
docker run -d -p 6379:6379 -e REDIS_PASSWORD=password bitnami/redis:4.0.11-r6
```

### Build & Run
```zsh
# build
$ ./gradlew clean build
# run
$ ./gradlew bootRun
```
            
---

### 구현 리스트
 - [x] Spring + webflux를 적용하여 기능 개발
     - Spring + webflux 구현
 - [x] 인증 기능 개발
     - JWT 토큰 발행, 인증 구현
 - [x] Redis 사용하여 부가 기능 개발
     - redis pub/sub 구현, 현재 단일채널로 고정
 - [ ] Reactive MongoDB를 적용하여 최근 채팅 메시지 가져오기

---


### API 설계
- 로그인 `POST /api/login`
```
  - req        
      - body : {
                 "email": "test1@test.com", 
                 "password": "123456" 
               }
  - res
      - data: { user: UserInfo, token: "token" }
```

### ws 설계
- connection
  - `ws://localhost:8080/redis-chat?token=test`
  - JWT 인증토큰을 param으로 전달 받아 인증체크
