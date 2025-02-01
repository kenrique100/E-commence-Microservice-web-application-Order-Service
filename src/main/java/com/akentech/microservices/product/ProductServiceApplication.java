package com.akentech.microservices.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * Main application class for the Product Service.
 */
@SpringBootApplication
@EnableReactiveMongoRepositories(basePackages = "com.akentech.microservices.product.repository")
public class ProductServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
}