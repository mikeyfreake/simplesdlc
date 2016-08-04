package com.mycompany.myapp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mycompany.myapp.domain.PersistentToken;
import com.mycompany.myapp.domain.User;

/**
 * Spring Data MongoDB repository for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends MongoRepository<PersistentToken, String> {

    List<PersistentToken> findByUser(User user);

    List<PersistentToken> findByTokenDateBefore(LocalDate localDate);

}
