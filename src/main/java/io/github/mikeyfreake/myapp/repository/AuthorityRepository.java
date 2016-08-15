package io.github.mikeyfreake.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.mikeyfreake.myapp.domain.Authority;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
