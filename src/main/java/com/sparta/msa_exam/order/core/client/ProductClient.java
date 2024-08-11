package com.sparta.msa_exam.order.core.client;

import com.sparta.msa_exam.order.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductClient {
	@GetMapping("/products/{id}")
	ProductResponseDto getProduct(@PathVariable("id") Long productId);

	@GetMapping("/products/{id}/reduceQuantity")
	void reduceProductQuantity(@PathVariable("id") Long productId, @RequestParam("quantity") int quantity);

	/*
	@GetMapping("/product/{id}")
	String getProduct(@PathVariable("id") String id);

	@GetMapping("/productDetail/{productId}")
	Product getProductDetail(@PathVariable("productId") String productId);

	 */
}
