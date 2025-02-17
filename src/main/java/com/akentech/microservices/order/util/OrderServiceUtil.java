package com.akentech.microservices.order.util;

import com.akentech.microservices.order.dto.OrderRequest;
import com.akentech.microservices.order.exception.OrderNotFoundException;
import com.akentech.microservices.order.model.Order;
import com.akentech.microservices.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class OrderServiceUtil {

    private OrderServiceUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a new Order object from OrderRequest.
     *
     * @param orderRequest The request containing order details.
     * @return The created Order object.
     * @throws IllegalArgumentException if the request has invalid data.
     */
    public static Order mapToOrder(OrderRequest orderRequest) {
        validateOrderRequest(orderRequest); // Ensure valid data before processing

        Order order = new Order();
        order.setOrderNumber(orderRequest.orderNumber() != null ? orderRequest.orderNumber() : UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setSkuCode(orderRequest.skuCode());
        order.setQuantity(orderRequest.quantity());

        log.info("Mapped OrderRequest to Order with SKU: {}", order.getSkuCode());
        return order;
    }

    /**
     * Finds an order by ID or throws an OrderNotFoundException.
     *
     * @param orderRepository The repository to fetch the order.
     * @param id The order ID to search for.
     * @return The found Order object.
     */
    public static Order findOrderByIdOrThrow(OrderRepository orderRepository, Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", id);
                    return new OrderNotFoundException("Order not found with id: " + id);
                });
    }

    /**
     * Validates the OrderRequest data.
     *
     * @param orderRequest The order request to validate.
     * @throws IllegalArgumentException if validation fails.
     */
    private static void validateOrderRequest(OrderRequest orderRequest) {
        if (orderRequest == null || orderRequest.skuCode() == null || orderRequest.skuCode().isEmpty()) {
            throw new IllegalArgumentException("Invalid OrderRequest: SKU code is required.");
        }
        if (orderRequest.quantity() == null || orderRequest.quantity() <= 0) {
            throw new IllegalArgumentException("Invalid OrderRequest: Quantity must be greater than 0.");
        }
        if (orderRequest.price() == null || orderRequest.price().doubleValue() <= 0) {
            throw new IllegalArgumentException("Invalid OrderRequest: Price must be greater than 0.");
        }
    }
}