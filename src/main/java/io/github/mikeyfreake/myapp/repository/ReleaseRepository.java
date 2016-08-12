package io.github.mikeyfreake.myapp.repository;

import io.github.mikeyfreake.myapp.domain.Release;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Release entity.
 */
@SuppressWarnings("unused")
public interface ReleaseRepository extends JpaRepository<Release,Long> {

    @Query("select release from Release release where release.assignedTo.login = ?#{principal.username}")
    List<Release> findByAssignedToIsCurrentUser();

}
