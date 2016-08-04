package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Product;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Product entity.
 */
@SuppressWarnings("unused")
@JaversSpringDataAuditable
public interface ProductRepository extends MongoRepository<Product,String> {

}
