package io.github.mikeyfreake.myapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import io.github.mikeyfreake.myapp.domain.enumeration.StoryClassification;
import io.github.mikeyfreake.myapp.domain.enumeration.StoryPriority;
import io.github.mikeyfreake.myapp.domain.enumeration.StoryState;
import io.github.mikeyfreake.myapp.domain.enumeration.StoryUrgency;

/**
 * A Story.
 */
@Entity
@Table(name = "story")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "story")
public class Story extends AbstractAuditingEntity implements Serializable {

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

    @Column(name = "acceptance_criteria")
    private String acceptanceCriteria;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification")
    private StoryClassification classification;

    @Column(name = "effort_points")
    private Integer effortPoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private StoryState state;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency")
    private StoryUrgency urgency;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private StoryPriority priority;

    @ManyToOne
    @NotNull
    private Product product;

    @ManyToOne
    private Release release;

    @OneToOne
    @JoinColumn(unique = true)
    private User assignedTo;

    @OneToOne
    @JoinColumn(unique = true)
    private User requestedBy;

    @ManyToOne
    private Sprint sprint;

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

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public StoryClassification getClassification() {
        return classification;
    }

    public void setClassification(StoryClassification classification) {
        this.classification = classification;
    }

    public Integer getEffortPoints() {
        return effortPoints;
    }

    public void setEffortPoints(Integer effortPoints) {
        this.effortPoints = effortPoints;
    }

    public StoryState getState() {
        return state;
    }

    public void setState(StoryState state) {
        this.state = state;
    }

    public StoryUrgency getUrgency() {
        return urgency;
    }

    public void setUrgency(StoryUrgency urgency) {
        this.urgency = urgency;
    }

    public StoryPriority getPriority() {
        return priority;
    }

    public void setPriority(StoryPriority priority) {
        this.priority = priority;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User user) {
        this.assignedTo = user;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User user) {
        this.requestedBy = user;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Story story = (Story) o;
        if(story.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, story.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Story{" +
            "id=" + id +
            ", shortDescription='" + shortDescription + "'" +
            ", description='" + description + "'" +
            ", acceptanceCriteria='" + acceptanceCriteria + "'" +
            ", classification='" + classification + "'" +
            ", effortPoints='" + effortPoints + "'" +
            ", state='" + state + "'" +
            ", urgency='" + urgency + "'" +
            ", priority='" + priority + "'" +
            '}';
    }
}
