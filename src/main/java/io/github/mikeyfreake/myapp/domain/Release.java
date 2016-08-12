package io.github.mikeyfreake.myapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import io.github.mikeyfreake.myapp.domain.enumeration.ReleaseState;

/**
 * A Release.
 */
@Entity
@Table(name = "release")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "release")
public class Release extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private ReleaseState state;

    @Column(name = "planned_start_date")
    private ZonedDateTime plannedStartDate;

    @Column(name = "planned_end_date")
    private ZonedDateTime plannedEndDate;

    @NotNull
    @Size(max = 128)
    @Column(name = "short_description", length = 128, nullable = false)
    private String shortDescription;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @NotNull
    private User assignedTo;

    @ManyToOne
    @NotNull
    private Product product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReleaseState getState() {
        return state;
    }

    public void setState(ReleaseState state) {
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

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User user) {
        this.assignedTo = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Release release = (Release) o;
        if(release.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, release.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Release{" +
            "id=" + id +
            ", state='" + state + "'" +
            ", plannedStartDate='" + plannedStartDate + "'" +
            ", plannedEndDate='" + plannedEndDate + "'" +
            ", shortDescription='" + shortDescription + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
