package com.akentech.microservice.product.service;

import com.akentech.microservice.product.dto.ProductRequest;
import com.akentech.microservice.product.dto.ProductResponse;
import com.akentech.microservice.product.model.Product;
import com.akentech.microservice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service implementation for managing product operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .id(productRequest.id())  // Ensure ID is included
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();

        return productRepository.save(product)
                .doOnSuccess(savedProduct -> log.info("Product Created Successfully: {}", savedProduct.getId()))
                .map(savedProduct -> new ProductResponse(savedProduct.getId(), savedProduct.getName(), savedProduct.getDescription(), savedProduct.getPrice()));
    }

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .doOnComplete(() -> log.info("Retrieved all products"))
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice()));
    }

    @Override
    public Mono<ProductResponse> getProductById(String id) {
        return productRepository.findById(id)
                .doOnSuccess(product -> log.info("Retrieved product: {}", product.getId()))
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice()));
    }

    @Override
    public Mono<ProductResponse> updateProduct(String id, ProductRequest productRequest) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(productRequest.name());
                    existingProduct.setDescription(productRequest.description());
                    existingProduct.setPrice(productRequest.price());
                    return productRepository.save(existingProduct);
                })
                .doOnSuccess(updatedProduct -> log.info("Product Updated Successfully: {}", updatedProduct.getId()))
                .map(updatedProduct -> new ProductResponse(updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getDescription(), updatedProduct.getPrice()));
    }

    @Override
    public Mono<Void> deleteProduct(String id) {
        return productRepository.deleteById(id)
                .doOnSuccess(voidValue -> log.info("Product Deleted Successfully: {}", id));
    }
}