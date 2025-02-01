package com.akentech.microservices.product.controller;

import com.akentech.microservices.product.dto.ProductRequest;
import com.akentech.microservices.product.dto.ProductResponse;
import com.akentech.microservices.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing product operations.
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * Creates a new product.
     *
     * @param productRequest the product details to create.
     * @return the created product response.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    /**
     * Retrieves all products.
     *
     * @return a flux of product responses.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve.
     * @return the product response.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductResponse> getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    /**
     * Updates an existing product.
     *
     * @param id             the ID of the product to update.
     * @param productRequest the updated product details.
     * @return the updated product response.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductResponse> updateProduct(@PathVariable String id, @RequestBody ProductRequest productRequest) {
        return productService.updateProduct(id, productRequest);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete.
     * @return a mono indicating completion.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable String id) {
        return productService.deleteProduct(id);
    }
}