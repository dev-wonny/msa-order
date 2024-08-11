package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.core.client.ProductClient;
import com.sparta.msa_exam.order.core.domain.Order;
import com.sparta.msa_exam.order.core.enums.OrderStatus;
import com.sparta.msa_exam.order.dto.OrderRequestDto;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.order.dto.OrderSearchDto;
import com.sparta.msa_exam.order.dto.ProductResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class OrderService {
	//order -> product 여러 서비스 중에 하나 호출
	private final ProductClient productClient;
	private final OrderRepository orderRepository;

	public OrderService(ProductClient productClient, OrderRepository orderRepository) {
		this.productClient = productClient;
		this.orderRepository = orderRepository;
	}

	/**
	 * 주문 생성
	 * @param requestDto
	 * @param userId
	 * @return
	 */
	@Transactional
	public OrderResponseDto createOrder(OrderRequestDto requestDto, String userId) {
		// Check if products exist and if they have enough quantity

		// 상품이 많으면 For문으로 돌려야한다.
		// DB에서 조건절로 바꾼다
		// 주문에 속한 product_id 있는지 체크
		for (Long productId : requestDto.getOrderItemIds()) {
			ProductResponseDto product = productClient.getProduct(productId);
			log.info("############################ Product 수량 확인 : " + product.getQuantity());
			if (product.getQuantity() < 1) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with ID " + productId + " is out of stock.");
			}
		}

		// 상태 처리

		// [MSA 문제점] 상품은 줄였다 -> 방어로직(상품을 늘린다)
		// Reduce the quantity of each product by 1
		for (Long productId : requestDto.getOrderItemIds()) {
			productClient.reduceProductQuantity(productId, 1);
		}


		// [MSA 문제점] 주문은 못했다
		Order order = Order.createOrder(requestDto.getOrderItemIds(), userId);
		Order savedOrder = orderRepository.save(order);
		return toResponseDto(savedOrder);
	}

	/**
	 * 주문 페이지 조회
	 * @param searchDto
	 * @param pageable
	 * @param role
	 * @param userId
	 * @return
	 */
	public Page<OrderResponseDto> getOrders(OrderSearchDto searchDto, Pageable pageable, String role, String userId) {
		return orderRepository.searchOrders(searchDto, pageable, role, userId);
	}

	/**
	 * orderId 로 단건 주문 조회
	 * @param orderId
	 * @return
	 */
	@Transactional(readOnly = true)
	public OrderResponseDto getOrderById(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.filter(o -> o.getDeletedAt() == null)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
		return toResponseDto(order);
	}

	/**
	 * 주문 수정
	 * @param orderId
	 * @param requestDto
	 * @param userId
	 * @return
	 */
	@Transactional
	public OrderResponseDto updateOrder(Long orderId, OrderRequestDto requestDto, String userId) {
		Order order = orderRepository.findById(orderId)
				.filter(o -> o.getDeletedAt() == null)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));

		order.updateOrder(requestDto.getOrderItemIds(), userId, OrderStatus.valueOf(requestDto.getStatus()));
		Order updatedOrder = orderRepository.save(order);

		return toResponseDto(updatedOrder);
	}

	/**
	 * 주문 삭제
	 * @param orderId
	 * @param deletedBy
	 */
	@Transactional
	public void deleteOrder(Long orderId, String deletedBy) {
		Order order = orderRepository.findById(orderId)
				.filter(o -> o.getDeletedAt() == null)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
		order.deleteOrder(deletedBy);
		orderRepository.save(order);
	}

	private OrderResponseDto toResponseDto(Order order) {
		return new OrderResponseDto(
				order.getOrder_id(),
				order.getStatus().name(),
				order.getCreatedAt(),
				order.getCreatedBy(),
				order.getUpdatedAt(),
				order.getUpdatedBy(),
				order.getOrderItemIds()
		);
	}

	/*
	public String getProductInfo(String orderId) {
		if (orderId.equals("1")) {
			String productId = orderId;

			String productInfo = productClient.getProduct(productId);

			return "Your Order is " + orderId + " and product id is " + productInfo;
		}
		return "Not exist order";
	}

	 */
}
