package com.akentech.microservices.order.service;

import com.akentech.microservices.common.dto.InventoryRequest;
import com.akentech.microservices.order.client.InventoryClient;
import com.akentech.microservices.order.dto.OrderRequest;
import com.akentech.microservices.order.dto.OrderResponse;
import com.akentech.microservices.order.exception.OrderNotFoundException;
import com.akentech.microservices.order.exception.OutOfStockException;
import com.akentech.microservices.order.model.Order;
import com.akentech.microservices.order.repository.OrderRepository;
import com.akentech.microservices.order.util.OrderServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    @Override
    public void placeOrder(OrderRequest orderRequest) {
        // Create an InventoryRequest object
        var inventoryRequest = new InventoryRequest(
                orderRequest.skuCode(), orderRequest.quantity());

        // Check inventory availability
        var inventoryResponse = inventoryClient.checkInventory(inventoryRequest);

        if (!inventoryResponse.isInStock()) {
            throw new OutOfStockException("Product with SKU code " + orderRequest.skuCode() + " is out of stock.");
        }

        // Create and save the order using the utility method
        Order order = OrderServiceUtil.mapToOrder(orderRequest);
        orderRepository.save(order);

        log.info("Order placed successfully with ID: {}", order.getId());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = OrderServiceUtil.findOrderByIdOrThrow(orderRepository, id);
        return mapToOrderResponse(order);
    }

    @Override
    public void updateOrder(Long id, OrderRequest orderRequest) {
        Order order = OrderServiceUtil.findOrderByIdOrThrow(orderRepository, id);

        // Do not update the orderNumber if it is null in the request
        if (orderRequest.orderNumber() != null) {
            order.setOrderNumber(orderRequest.orderNumber());
        }

        order.setSkuCode(orderRequest.skuCode());
        order.setPrice(orderRequest.price());
        order.setQuantity(orderRequest.quantity());
        orderRepository.save(order);

        log.info("Order updated successfully with ID: {}", id);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
        log.info("Order deleted successfully with ID: {}", id);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getSkuCode(),
                order.getPrice(),
                order.getQuantity()
        );
    }
}