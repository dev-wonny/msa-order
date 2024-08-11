package com.sparta.msa_exam.order.orders;

import static com.sparta.msa_exam.order.core.domain.QOrder.order;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.msa_exam.order.core.domain.Order;
import com.sparta.msa_exam.order.core.enums.OrderStatus;
import com.sparta.msa_exam.order.core.enums.Role;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import com.sparta.msa_exam.order.dto.OrderSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderRepositoryImpl implements OrderRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public OrderRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	/**
	 * 주문 조회: 상태, 주문 목록, 정렬, 페이징
	 * @param searchDto
	 * @param pageable
	 * @param role
	 * @param userId
	 * @return
	 */
	@Override
	public Page<OrderResponseDto> searchOrders(OrderSearchDto searchDto, Pageable pageable, String role, String userId) {

		final List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

		QueryResults<Order> results = queryFactory
				.selectFrom(order)
				// where절 검색
				.where(
						statusEq(searchDto.getStatus()),
						orderItemIdsIn(searchDto.getOrderItemIds()),
						userCheck(role, userId)
				)
				.orderBy(orders.toArray(new OrderSpecifier[0]))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();

		final List<OrderResponseDto> content = results.getResults().stream()
				.map(Order::toResponseDto)
				.collect(Collectors.toList());
		final long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression statusEq(OrderStatus status) {
		return status != null ? order.status.eq(status) : null;
	}

	private BooleanExpression userCheck(String role, String userId) {

		final Role userRole = Role.getRoleByName(role);

		return userRole.isMember() ? order.createdBy.eq(userId) : null;
	}

	private BooleanExpression orderItemIdsIn(List<Long> orderItemIds) {
		return orderItemIds != null && !orderItemIds.isEmpty() ? order.orderItemIds.any().in(orderItemIds) : null;
	}

	/**
	 * 조건에 따른 정렬
	 * @param pageable
	 * @return
	 */
	private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier<?>> orders = new ArrayList<>();

		if (pageable.getSort() != null) {
			for (Sort.Order sortOrder : pageable.getSort()) {
				com.querydsl.core.types.Order direction = sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
				switch (sortOrder.getProperty()) {
					case "createdAt":
						orders.add(new OrderSpecifier<>(direction, order.createdAt));
						break;
					case "status":
						orders.add(new OrderSpecifier<>(direction, order.status));
						break;
					default:
						break;
				}
			}
		}

		return orders;
	}
}
