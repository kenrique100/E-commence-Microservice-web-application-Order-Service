package com.akentech.microservices.order.client;

import com.akentech.microservices.common.dto.InventoryRequest;
import com.akentech.microservices.common.dto.InventoryResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;

public interface InventoryClient {

    @GetExchange("/check")
    InventoryResponse checkInventory(@RequestBody InventoryRequest inventoryRequest);
}