package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.core.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
}