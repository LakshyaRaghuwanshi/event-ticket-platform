package com.spring_project.Event_Ticket_Platform.util;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public class JwtUtil {
    private JwtUtil() {

    }

    public static UUID parseUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

}
