package com.mycompany.myapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mycompany.myapp.domain.Authority;

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
public interface AuthorityRepository extends MongoRepository<Authority, String> {
}
