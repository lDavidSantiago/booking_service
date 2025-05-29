package com.ldavidsantiago.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Claims extractClaimsWithoutValidation(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token");
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claimsMap = objectMapper.readValue(payloadJson, Map.class);
            return new DefaultClaims(claimsMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract claims from token", e);
        }
    }
}