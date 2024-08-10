package com.sparta.msa_exam.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping("/order/{orderId}")
	public String getOrder(@PathVariable("orderId") String orderId) {
		return orderService.getProductInfo(orderId);
	}
}
