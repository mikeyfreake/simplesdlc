package io.github.mikeyfreake.myapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.github.mikeyfreake.myapp.domain.Release;

/**
 * Spring Data JPA repository for the Release entity.
 */
@SuppressWarnings("unused")
public interface ReleaseRepository extends JpaRepository<Release,Long> {

    @Query("select release from Release release where release.assignedTo.login = ?#{principal.username}")
    List<Release> findByAssignedToIsCurrentUser();

}
