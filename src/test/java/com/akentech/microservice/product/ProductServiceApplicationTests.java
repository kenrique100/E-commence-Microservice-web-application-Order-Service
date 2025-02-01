package com.akentech.microservice.product;

import com.akentech.microservice.product.dto.ProductRequest;
import com.akentech.microservice.product.dto.ProductResponse;
import com.akentech.microservice.product.model.Product;
import com.akentech.microservice.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private ProductRepository productRepository;

	@Test
	void shouldCreateProduct() {
		ProductRequest productRequest = new ProductRequest(null, "SUV Toyota Honda", "Black SUV with 4WD", BigDecimal.valueOf(10000000));

		webTestClient.post()
				.uri("/api/product")
				.body(Mono.just(productRequest), ProductRequest.class)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(ProductResponse.class)
				.value(response -> {
					Assertions.assertNotNull(response.id());
					Assertions.assertEquals(productRequest.name(), response.name());
					Assertions.assertEquals(productRequest.description(), response.description());
					Assertions.assertEquals(productRequest.price(), response.price());
				});
	}

	@Test
	void shouldGetAllProducts() {
		webTestClient.get()
				.uri("/api/product")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(ProductResponse.class);
	}

	@Test
	void shouldGetProductById() {
		Product product = Product.builder()
				.id("test-id")
				.name("SUV Toyota Honda")
				.description("Black SUV with 4WD")
				.price(BigDecimal.valueOf(10000000))
				.build();
		productRepository.save(product).block();

		webTestClient.get()
				.uri("/api/product/{id}", product.getId())
				.exchange()
				.expectStatus().isOk()
				.expectBody(ProductResponse.class)
				.value(response -> {
					Assertions.assertEquals(product.getId(), response.id());
					Assertions.assertEquals(product.getName(), response.name());
					Assertions.assertEquals(product.getDescription(), response.description());
					Assertions.assertEquals(product.getPrice(), response.price());
				});
	}

	@Test
	void shouldDeleteProduct() {
		// Step 1: Create and save a product to the database
		Product product = Product.builder()
				.id("delete-test-id")
				.name("SUV Toyota Honda")
				.description("Black SUV with 4WD")
				.price(BigDecimal.valueOf(10000000))
				.build();
		productRepository.save(product).block();

		// Step 2: Delete the product using the DELETE endpoint
		webTestClient.delete()
				.uri("/api/product/{id}", product.getId())
				.exchange()
				.expectStatus().isNoContent();

		// Step 3: Verify that the product no longer exists in the database
		webTestClient.get()
				.uri("/api/product/{id}", product.getId())
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void shouldReturnNoContentWhenDeletingNonExistentProduct() {
		// Try to delete a product that does not exist
		String nonExistentId = "non-existent-id";

		webTestClient.delete()
				.uri("/api/product/{id}", nonExistentId)
				.exchange()
				.expectStatus().isNoContent();
	}
}
