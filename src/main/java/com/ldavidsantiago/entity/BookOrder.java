package com.ldavidsantiago.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="book_order_tb")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookOrder {
    @Id
    private Integer id;
    @JsonIgnore
    private String name;

    private Integer quantity;
    @JsonIgnore
    private double price;
}
