package com.line.games.api;

import com.line.games.ApplicationTest;
import com.line.games.model.User;
import com.line.games.service.JwtService;
import com.line.games.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthTest extends ApplicationTest {

    @Autowired
    private UserService userService;
    @Autowired
    JwtService jwtService;

    @Test
    public void verify() {
        String email = "test@test.com";
        String token = jwtService.getToken(
                User.builder().email(email).password("123456").name("user1")
                        .build());
        User user = jwtService.verify(token);
        Assert.assertTrue(user.getEmail().equals(email));
    }
}
