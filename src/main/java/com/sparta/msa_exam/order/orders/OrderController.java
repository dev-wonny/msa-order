package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.core.enums.Role;
import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.order.dto.OrderSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/order")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * 주문 생성
	 *
	 * @param orderRequestDto
	 * @param userId
	 * @param role
	 * @return
	 */
	@PostMapping
	public OrderResponseDto createOrder(@RequestBody OrderRequestDto orderRequestDto,
	                                    @RequestHeader(value = "X-User-Id", required = true) String userId,
	                                    @RequestHeader(value = "X-Role", required = true) String role) {

		return orderService.createOrder(orderRequestDto, userId);
	}

	/**
	 * 주문 페이지 조회
	 *
	 * @param searchDto
	 * @param pageable
	 * @param userId
	 * @param role
	 * @return
	 */
	@GetMapping
	public Page<OrderResponseDto> getOrders(OrderSearchDto searchDto, Pageable pageable,
	                                        @RequestHeader(value = "X-User-Id", required = true) String userId,
	                                        @RequestHeader(value = "X-Role", required = true) String role) {

		final Role userRole = Role.getRoleByName(role);
		// 역할이 MANAGER인지 확인
		if (!userRole.isManager()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. User role is not MANAGER.");
		}
		return orderService.getOrders(searchDto, pageable, role, userId);
	}

	/**
	 * 주문 단건 조회
	 *
	 * @param orderId
	 * @return
	 */
	@GetMapping("/{orderId}")
	public OrderResponseDto getOrderById(@PathVariable Long orderId) {
		return orderService.getOrderById(orderId);
	}

	/**
	 * 주문 수정
	 *
	 * @param orderId
	 * @param orderRequestDto
	 * @param userId
	 * @param role
	 * @return
	 */
	@PutMapping("/{orderId}")
	public OrderResponseDto updateOrder(@PathVariable Long orderId,
	                                    @RequestBody OrderRequestDto orderRequestDto,
	                                    @RequestHeader(value = "X-User-Id", required = true) String userId,
	                                    @RequestHeader(value = "X-Role", required = true) String role) {
		return orderService.updateOrder(orderId, orderRequestDto, userId);
	}

	/**
	 * 주문 삭제
	 *
	 * @param orderId
	 * @param deletedBy
	 */
	@DeleteMapping("/{orderId}")
	public ResponseEntity deleteOrder(@PathVariable Long orderId, @RequestParam String deletedBy) {
		orderService.deleteOrder(orderId, deletedBy);
		return ResponseEntity.noContent().build();
	}

	/*
	@GetMapping("/test/{orderId}")
	public String getOrder(@PathVariable("orderId") String orderId) {
		return orderService.getProductInfo(orderId);
	}

	 */
}
