package com.akentech.microservices.product.service;

import com.akentech.microservices.product.dto.ProductRequest;
import com.akentech.microservices.product.dto.ProductResponse;
import com.akentech.microservices.product.exception.ProductNotFoundException;
import com.akentech.microservices.product.model.Product;
import com.akentech.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service implementation for handling product-related operations.
 * Uses Spring WebFlux for reactive programming.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Creates a new product in the database.
     * @param productRequest DTO containing product details.
     * @return Mono<ProductResponse> representing the saved product.
     */
    @Override
    public Mono<ProductResponse> createProduct(ProductRequest productRequest) {

        // Build a new Product entity from the incoming request DTO.
        Product product = Product.builder()
                .id(productRequest.id())
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();

        // Save the product and return a response DTO.
        return productRepository.save(product)
                .doOnSuccess(savedProduct -> log.info("Product Created Successfully: {}", savedProduct.getId()))
                .map(savedProduct -> new ProductResponse(savedProduct.getId(), savedProduct.getName(), savedProduct.getDescription(), savedProduct.getPrice()));
    }

    /**
     * Retrieves all products from the database.
     * @return Flux<ProductResponse> containing all products.
     */
    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .doOnComplete(() -> log.info("Retrieved all products"))
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice()));
    }

    /**
     * Retrieves a product by its ID.
     * @param id The ID of the product.
     * @return Mono<ProductResponse> containing the product details if found.
     */
    @Override
    public Mono<ProductResponse> getProductById(String id) {
        return productRepository.findById(id)

                // Handle case where product is not found.
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found with id: " + id)))
                .doOnSuccess(product -> log.info("Retrieved product: {}", product.getId()))
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice()));
    }

    /**
     * Updates an existing product.
     * @param id The ID of the product to update.
     * @param productRequest DTO containing updated product details.
     * @return Mono<ProductResponse> representing the updated product.
     */
    @Override
    public Mono<ProductResponse> updateProduct(String id, ProductRequest productRequest) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found with id: " + id)))
                .flatMap(existingProduct -> {

                    // Update product fields with new values.
                    existingProduct.setName(productRequest.name());
                    existingProduct.setDescription(productRequest.description());
                    existingProduct.setPrice(productRequest.price());
                    return productRepository.save(existingProduct);
                })
                .doOnSuccess(updatedProduct -> log.info("Product Updated Successfully: {}", updatedProduct.getId()))
                .map(updatedProduct -> new ProductResponse(updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getDescription(), updatedProduct.getPrice()));
    }

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @return Mono<Void> indicating completion.
     */
    @Override
    public Mono<Void> deleteProduct(String id) {
        return productRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return productRepository.deleteById(id)
                                .doOnSuccess(voidValue -> log.info("Product Deleted Successfully: {}", id));
                    } else {
                        return Mono.error(new ProductNotFoundException("Product not found with id: " + id));
                    }
                });
    }
}
