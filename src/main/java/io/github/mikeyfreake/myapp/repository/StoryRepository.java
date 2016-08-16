package io.github.mikeyfreake.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.mikeyfreake.myapp.domain.Story;

/**
 * Spring Data JPA repository for the Story entity.
 */
@SuppressWarnings("unused")
public interface StoryRepository extends JpaRepository<Story,Long> {

}
