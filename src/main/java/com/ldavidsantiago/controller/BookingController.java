package com.ldavidsantiago.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ldavidsantiago.common.BookingRequest;
import com.ldavidsantiago.common.BookingResponse;
import com.ldavidsantiago.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("book")
public class BookingController {

    @Value("${microservice.auth-service.endpoints.endpoint.uri}")
    private String validateTokenUri;

    @Value("${microservice.product-microservice.endpoints.endpoint.uri}")
    private String productUri;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RestTemplate restTemplate;  // <-- Inyectamos el RestTemplate

    @PostMapping("/bookOrder")
    public BookingResponse bookOrder(
            @RequestBody BookingRequest bookingRequest,
            @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
    try {
        Long productId = Long.valueOf(bookingRequest.getBookOrder().getId());
        String responseProduct = restTemplate.getForObject(productUri + "/" + productId, String.class);
        if (responseProduct.equals("error")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, responseProduct);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseProduct);
        String productName = jsonNode.get("nombreProducto").asText();
        int valor = jsonNode.get("precio").asInt();
        bookingRequest.getBookOrder().setName(productName);
        bookingRequest.getBookOrder().setPrice(valor * bookingRequest.getBookOrder().getQuantity());

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
            return bookingService.bookOder(bookingRequest, token);
        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token vencido o invalido : " + e.getMessage());
        }
    }catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token vencido o invalido : " + e.getMessage());
    }
    }
}
