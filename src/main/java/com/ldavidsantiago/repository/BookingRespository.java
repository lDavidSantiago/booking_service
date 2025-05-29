package com.ldavidsantiago.repository;

import com.ldavidsantiago.entity.BookOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRespository extends JpaRepository<BookOrder, Integer> {
}
