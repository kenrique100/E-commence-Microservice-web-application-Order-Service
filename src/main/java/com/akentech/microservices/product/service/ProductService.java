package com.akentech.microservices.product.service;

import com.akentech.microservices.product.dto.ProductRequest;
import com.akentech.microservices.product.dto.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for managing product operations.
 */
public interface ProductService {
    Mono<ProductResponse> createProduct(ProductRequest productRequest);
    Flux<ProductResponse> getAllProducts();
    Mono<ProductResponse> getProductById(String id);
    Mono<ProductResponse> updateProduct(String id, ProductRequest productRequest);
    Mono<Void> deleteProduct(String id);
}