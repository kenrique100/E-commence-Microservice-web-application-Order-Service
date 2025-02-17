package com.akentech.microservices.order;

import com.akentech.microservices.common.dto.InventoryRequest;
import com.akentech.microservices.common.dto.InventoryResponse;
import com.akentech.microservices.order.client.InventoryClient;
import com.akentech.microservices.order.controller.OrderController;
import com.akentech.microservices.order.dto.OrderRequest;
import com.akentech.microservices.order.dto.OrderResponse;
import com.akentech.microservices.order.model.Order;
import com.akentech.microservices.order.repository.OrderRepository;
import com.akentech.microservices.order.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = TestcontainersConfiguration.class)
public class OrderServiceApplicationTests {

    @Autowired
    private OrderController orderController;

    @Autowired
    private OrderRepository orderRepository;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        // Manually inject the mocked InventoryClient into the OrderServiceImpl
        orderService = new OrderServiceImpl(orderRepository, inventoryClient);
    }

    @Test
    void testPlaceOrder_Success() {
        // Mock the InventoryClient to return a positive response
        Mockito.when(inventoryClient.checkInventory(any(InventoryRequest.class)))
                .thenReturn(new InventoryResponse("SKU123", true));

        // Create an OrderRequest
        OrderRequest orderRequest = new OrderRequest(
                null, "ORD123", "SKU123", BigDecimal.valueOf(100.00), 2);

        // Call the controller to place the order
        ResponseEntity<String> response = orderController.placeOrder(orderRequest);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Order Placed Successfully", response.getBody());

        // Verify that the order was saved in the database
        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());
        assertEquals("ORD123", orders.getFirst().getOrderNumber());
    }

    @Test
    void testPlaceOrder_OutOfStock() {
        // Mock the InventoryClient to return an out-of-stock response
        Mockito.when(inventoryClient.checkInventory(any(InventoryRequest.class)))
                .thenReturn(new InventoryResponse("sku973", false)); // isInStock = false

        // Create an OrderRequest with the provided example payload
        OrderRequest orderRequest = new OrderRequest(
                null, // orderId (null for new orders)
                null, // orderNumber (not required)
                "sku973", // skuCode
                BigDecimal.valueOf(5000.00), // price
                100 // quantity
        );

        // Call the controller to place the order
        ResponseEntity<String> response = orderController.placeOrder(orderRequest);

        // Verify the response status code and message
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product with SKU code sku973 is out of stock.", response.getBody());

        // Verify that no order was saved in the database
        List<Order> orders = orderRepository.findAll();
        assertEquals(0, orders.size());
    }
    @Test
    void testGetOrderById_Success() {
        // Save an order to the database
        Order order = new Order();
        order.setOrderNumber("ORD123");
        order.setSkuCode("SKU123");
        order.setPrice(BigDecimal.valueOf(100.00));
        order.setQuantity(2);
        orderRepository.save(order);

        // Call the controller to get the order by ID
        OrderResponse orderResponse = orderController.getOrderById(order.getId());

        // Verify the response
        assertEquals("ORD123", orderResponse.orderNumber());
        assertEquals("SKU123", orderResponse.skuCode());
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(orderResponse.price())); // Compare BigDecimal values
        assertEquals(2, orderResponse.quantity());
    }

    @Test
    void testGetAllOrders_Success() {
        // Save multiple orders to the database
        Order order1 = new Order();
        order1.setOrderNumber("ORD123");
        order1.setSkuCode("SKU123");
        order1.setPrice(BigDecimal.valueOf(100.00));
        order1.setQuantity(2);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setOrderNumber("ORD456");
        order2.setSkuCode("SKU456");
        order2.setPrice(BigDecimal.valueOf(200.00));
        order2.setQuantity(3);
        orderRepository.save(order2);

        // Call the controller to get all orders
        List<OrderResponse> orders = orderController.getAllOrders();

        // Verify the response
        assertEquals(2, orders.size());
        assertEquals("ORD123", orders.get(0).orderNumber());
        assertEquals("ORD456", orders.get(1).orderNumber());
    }
}