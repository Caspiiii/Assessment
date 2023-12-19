package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class Trait implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    @NotNull(message = "Trait can not be null")
    private String trait;

    @Column(length = 1000)
    @NotNull(message = "Explanation can not be null")
    private String explanation;

    @Column(nullable = false)
    @Min(1)
    @Max(3)
    private int level;

    @Column(nullable = false)
    private int maxPoints;

    public Trait(String trait, String explanation, int level, int maxPoints) {
        this.trait = trait;
        this.explanation = explanation;
        this.level = level;
        this.maxPoints = maxPoints;
    }

    public Trait() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrait() {
        return trait;
    }

    public void setTrait(String trait) {
        this.trait = trait;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trait trait1 = (Trait) o;
        return level == trait1.level && maxPoints == trait1.maxPoints && Objects.equals(id, trait1.id) && Objects.equals(trait, trait1.trait) && Objects.equals(explanation, trait1.explanation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trait, explanation, level, maxPoints);
    }

    @Override
    public String toString() {
        return "Trait{"
            + "id=" + id + ", trait='" + trait + '\''
            + ", explanation='" + explanation + '\''
            + ", level=" + level
            + ", maxPoints=" + maxPoints
            + '}';
    }


}
