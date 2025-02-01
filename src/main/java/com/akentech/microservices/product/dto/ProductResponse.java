package com.akentech.microservices.product.dto;

import java.math.BigDecimal;

/**
 * Represents a response containing product details.
 */
public record ProductResponse(String id, String name, String description, BigDecimal price) {
}