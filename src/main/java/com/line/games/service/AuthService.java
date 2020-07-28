package com.line.games.service;

import com.line.games.exception.JwtAuthenticationException;
import com.line.games.model.User;
import com.line.games.util.Security;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     *  패스워드 체크
     */
    public boolean authenticate(String password, String encPassword) {
        return Security.match(password, encPassword);
    }

    /**
     * 인증 체크, 유저정보 가져오기
     */
    public User authenticate(Authentication authentication) {
        User user = jwtService.verify((String) authentication.getCredentials());
        return Optional.ofNullable(user).map(res -> user)
                .orElseThrow(() -> new JwtAuthenticationException("Failed to verify token", null));
    }

}