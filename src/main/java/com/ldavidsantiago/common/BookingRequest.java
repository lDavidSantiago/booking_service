package com.ldavidsantiago.common;

import com.ldavidsantiago.entity.BookOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private BookOrder bookOrder;
}
