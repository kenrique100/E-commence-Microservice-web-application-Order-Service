package com.akentech.microservices.order.client;

import com.akentech.microservices.common.dto.InventoryRequest;
import com.akentech.microservices.common.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service", url = "http://localhost:8087/api/inventory")
public interface InventoryClient {

    @PostMapping("/check")
    InventoryResponse checkInventory(@RequestBody InventoryRequest inventoryRequest);
}