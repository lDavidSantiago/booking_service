package com.ldavidsantiago.controller;

import com.ldavidsantiago.common.BookingRequest;
import com.ldavidsantiago.common.BookingResponse;
import com.ldavidsantiago.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("book")
public class BookingController {

    @Value("${microservice.auth-service.endpoints.endpoint.uri}")
    private String validateTokenUri;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RestTemplate restTemplate;  // <-- Inyectamos el RestTemplat

    @PostMapping("/bookOrder")
    public BookingResponse bookOrder(
            @RequestBody BookingRequest bookingRequest,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            System.out.println("Bearer token: " + token);
        } else {
            throw new RuntimeException("Token inválido o ausente");
        }
        String url = validateTokenUri + "?token=" + token;
        try {
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Response: " + response);
            return bookingService.bookOder(bookingRequest,token);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }
}
