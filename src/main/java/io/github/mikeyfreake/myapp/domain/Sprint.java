package io.github.mikeyfreake.myapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import io.github.mikeyfreake.myapp.domain.enumeration.SprintState;

/**
 * A Sprint.
 */
@Entity
@Table(name = "sprint")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "sprint")
public class Sprint extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "short_description", length = 128, nullable = false)
    private String shortDescription;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private SprintState state;

    @NotNull
    @Column(name = "planned_start_date", nullable = false)
    private ZonedDateTime plannedStartDate;

    @NotNull
    @Column(name = "planned_end_date", nullable = false)
    private ZonedDateTime plannedEndDate;

    @ManyToOne
    @NotNull
    private Release release;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SprintState getState() {
        return state;
    }

    public void setState(SprintState state) {
        this.state = state;
    }

    public ZonedDateTime getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(ZonedDateTime plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public ZonedDateTime getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(ZonedDateTime plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sprint sprint = (Sprint) o;
        if(sprint.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, sprint.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Sprint{" +
            "id=" + id +
            ", shortDescription='" + shortDescription + "'" +
            ", description='" + description + "'" +
            ", state='" + state + "'" +
            ", plannedStartDate='" + plannedStartDate + "'" +
            ", plannedEndDate='" + plannedEndDate + "'" +
            '}';
    }
}
