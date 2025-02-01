package com.akentech.microservice.product.service;

import com.akentech.microservice.product.dto.ProductRequest;
import com.akentech.microservice.product.dto.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<ProductResponse> createProduct(ProductRequest productRequest);
    Flux<ProductResponse> getAllProducts();
    Mono<ProductResponse> getProductById(String id);
    Mono<ProductResponse> updateProduct(String id, ProductRequest productRequest);
    Mono<Void> deleteProduct(String id);
}