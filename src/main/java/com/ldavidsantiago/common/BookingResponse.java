package com.ldavidsantiago.common;

import com.ldavidsantiago.entity.BookOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private double bookPrice;
    private String bookname;
    private BookOrder bookOrder;
    private Integer userId;
    private Double amount;
    private String transactionId;
    private String response;
}
