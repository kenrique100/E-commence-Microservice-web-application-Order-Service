package com.akentech.microservices.product.dto;

import java.math.BigDecimal;

/**
 * Represents a request to create or update a product.
 */
public record ProductRequest(String id, String name, String description, BigDecimal price) {
}