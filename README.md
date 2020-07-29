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
     - 미구현
 - [x] jquery활용하여 간단한 화면구성
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

- client test tool
  - [websocket-test-client](https://chrome.google.com/webstore/detail/websocket-test-client/fgponpodhbmadfljofbimhhlengambbn)
---

### 추가 기능: 멀티채널 구현
- 브랜치(mulit)에 백엔드 소스만 구현
- rest api auth인증은 미구현
- 멀티 채널(채팅방 생성) 구현 설계
  - 인증 받은 사용자가 api를 통하여 room을 생성하고 room이름으로 redis채널을 생성한다.
     - room name은 유니크 값이다.
     - mongodb에 연결하지 않아서 임시 Map에 이름을 key값으로 저장하여 관리한다.
  - room이 생성되면 인증된 사용자는 api를 통하여 방정보를 얻고 해당 redis채널에 ws접속이 가능하다. 
     - 예시 `ws://localhost:8080/redis-chat?token=test?room=name`
     - room 이름을 param으로 전달받아 해당 소켓을 채널에 등록시킨다.
     
