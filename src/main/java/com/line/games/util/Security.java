package com.line.games.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
public class Security {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     *  패스워드 체크
     */
    public static boolean match(String password, String encPassword) {
        return passwordEncoder.matches(password, encPassword);
    }

    /**
     * url param에서 토큰값을 추춣한다.
     */
    public static String getToken(String data) {
        try {
            MultiValueMap<String, String> parameters =
                    UriComponentsBuilder.fromUriString(data).build().getQueryParams();
            List<String> param = parameters.get("token");
            return param.get(0);
        } catch (Error e) {
            return "";
        }
    }
}
