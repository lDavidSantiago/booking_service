package com.ldavidsantiago.service;

import com.ldavidsantiago.common.BookingRequest;
import com.ldavidsantiago.common.BookingResponse;
import com.ldavidsantiago.common.Payment;
import com.ldavidsantiago.entity.BookOrder;
import com.ldavidsantiago.repository.BookingRespository;
import com.ldavidsantiago.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RefreshScope
public class BookingService {
    @Autowired
    private BookingRespository bookingRespository;
@Autowired
@Lazy
private RestTemplate restTemplate;
@Value("${microservice.payment-service.endpoints.endpoint.uri}")
private String baseUrl;


    public BookingResponse bookOder(BookingRequest bookingRequest , String token){
        Payment payment = new Payment();
        BookOrder bookOrder = bookingRequest.getBookOrder();
        payment.setAmount(bookOrder.getPrice());
        payment.setOrderId(bookOrder.getId());
        Integer userId = ((Number) JwtUtil.extractClaimsWithoutValidation(token).get("id")).intValue();
        System.out.println("User claims: " + userId);
        payment.setUserId(userId);
        Payment paymentResponse = restTemplate.postForObject(baseUrl, payment, Payment.class);
        String response = paymentResponse.getPaymentStatus().equals("Success")?"Payment processed Successful":"Payment Failure";
        bookingRespository.save(bookOrder);
        return new BookingResponse(bookOrder,userId,paymentResponse.getAmount(),paymentResponse.getTransactionId(),response);


    }
}
