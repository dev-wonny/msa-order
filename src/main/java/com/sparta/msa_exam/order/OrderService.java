package com.sparta.msa_exam.order;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
	//order -> product 여러 서비스 중에 하나 호출
	private final ProductClient productClient;

	public OrderService(ProductClient productClient) {
		this.productClient = productClient;
	}

	public String getProductInfo(String orderId) {
		if (orderId.equals("1")) {
			String productId = orderId;

			String productInfo = productClient.getProduct(productId);

			return "Your Order is " + orderId + " and product id is " + productInfo;
		}
		return "Not exist order";
	}
}
