package com.line.games.service;

import com.line.games.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtService {
    @Value("${jwt.expire.hours}")
    private Long expireHours;

    @Value("${jwt.token.secret}")
    private String plainSecret;
    private String encodedSecret;

    @PostConstruct
    protected void init() {
        this.encodedSecret = generateEncodedSecret(this.plainSecret);
    }

    /**
     * Jwt 생성
     */
    protected String generateEncodedSecret(String plainSecret) {
        if (StringUtils.isEmpty(plainSecret)) {
            throw new IllegalArgumentException("JWT secret cannot be null or empty.");
        }
        return Base64
                .getEncoder()
                .encodeToString(this.plainSecret.getBytes());
    }

    /**
     * Jwt 기간 체크
     */
    protected Date getExpirationTime() {
        Date now = new Date();
        Long expireInMilis = TimeUnit.HOURS.toMillis(expireHours);
        return new Date(expireInMilis + now.getTime());
    }

    /**
     * Jwt Token을 복호화 하여 유저정보를 얻는다
     */
    protected User getUser(String encodedSecret, String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(encodedSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return User.builder()
                    .id(Long.parseLong(claims.getId()))
                    .email(claims.getSubject())
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Jwt Token을 복호화 하여 이름을 얻는다.
     */
    protected String getToken(String encodedSecret, User user) {
        return Jwts.builder()
                .setId(user.getId().toString())
                .setSubject(user.getEmail())
                .claim("name", user.getName())
                .setIssuedAt(new Date())
                .setExpiration(getExpirationTime())
                .signWith(SignatureAlgorithm.HS512, encodedSecret)
                .compact();
    }

    public User getUser(String token) {
        return getUser(this.encodedSecret, token);
    }

    public String getToken(User user) {
        return getToken(this.encodedSecret, user);
    }

    public User verify(String token) {
        return getUser(token);
    }
}