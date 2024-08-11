package com.sparta.msa_exam.order.core.domain;

import com.sparta.msa_exam.order.core.enums.OrderStatus;
import com.sparta.msa_exam.order.dto.OrderResponseDto;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long order_id;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@ElementCollection
	@CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
	@Column(name = "order_item_id")
	private List<Long> orderItemIds;

	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;
	private LocalDateTime deletedAt;
	private String deletedBy;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		if (status == null) {
			status = OrderStatus.CREATED;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void updateStatus(OrderStatus status) {
		this.status = status;
		this.updatedAt = LocalDateTime.now();
	}

	// 팩토리 메서드
	public static Order createOrder(List<Long> orderItemIds, String createdBy) {
		return Order.builder()
				.orderItemIds(orderItemIds)
				.createdBy(createdBy)
				.status(OrderStatus.CREATED)
				.build();
	}

	// 업데이트 메서드
	public void updateOrder(List<Long> orderItemIds, String updatedBy, OrderStatus status) {
		this.orderItemIds = orderItemIds;
		this.updatedBy = updatedBy;
		this.status = status;
		this.updatedAt = LocalDateTime.now();
	}

	public void deleteOrder(String deletedBy) {
		this.deletedBy = deletedBy;
		this.deletedAt = LocalDateTime.now();
	}

	// DTO로 변환하는 메서드
	public OrderResponseDto toResponseDto() {
		return new OrderResponseDto(
				this.order_id,
				this.status.name(),
				this.createdAt,
				this.createdBy,
				this.updatedAt,
				this.updatedBy,
				this.orderItemIds
		);
	}
}

