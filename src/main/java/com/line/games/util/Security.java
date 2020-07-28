package com.line.games.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class Security {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";

    public static String encode(String text) {
        return passwordEncoder.encode(text);
    }

    public static boolean match(String password, String encPassword) {
        return passwordEncoder.matches(password, encPassword);
    }

    /**
     * Generate Token
     * @param n (length of token)
     * @return token
     */
    public static String generateToken(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
}
