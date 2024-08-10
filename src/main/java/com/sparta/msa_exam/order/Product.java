package com.sparta.msa_exam.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	private Long productId;
	private String name;
	private Integer supplyPrice;
}
