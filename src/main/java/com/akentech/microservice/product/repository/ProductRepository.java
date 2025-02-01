package com.akentech.microservice.product.repository;

import com.akentech.microservice.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository  extends MongoRepository<product, String> {
}
