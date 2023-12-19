package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
@DiscriminatorValue(value = "NT")
public class NeuroTrait extends Trait {


    /*@JoinTable(name = "causalities",
      joinColumns = @JoinColumn(name = "id"),
      inverseJoinColumns = @JoinColumn(name = "id"))*/
    @ManyToMany(fetch = FetchType.EAGER)
    //@Cascade({CascadeType.DELETE})
    @JoinColumn(name = "causalities")
    private List<PsychoTrait> causalities;


    public NeuroTrait(String trait, String explanation, int level, int maxPoints, List<PsychoTrait> causalities) {
        super(trait, explanation, level, maxPoints);
        this.causalities = causalities;
    }

    public NeuroTrait() {
    }

    public void setCausalities(List<PsychoTrait> causalities) {
        this.causalities = causalities;
    }

    public List<PsychoTrait> getCausalities() {
        return causalities;
    }


    public static final class NeuroTraitBuilder {
        private Long id;
        private String trait;
        private String explanation;
        private int level;
        private int maxPoints;

        private List<PsychoTrait> causalities;

        private NeuroTraitBuilder() {
        }

        public static NeuroTraitBuilder aNeuroTrait() {
            return new NeuroTraitBuilder();
        }

        public NeuroTraitBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public NeuroTraitBuilder withTrait(String trait) {
            this.trait = trait;
            return this;
        }

        public NeuroTraitBuilder withExplanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public NeuroTraitBuilder withLevel(int level) {
            this.level = level;
            return this;
        }

        public NeuroTraitBuilder withMaxPoints(int maxPoints) {
            this.maxPoints = maxPoints;
            return this;
        }

        public NeuroTraitBuilder withCausalities(List<PsychoTrait> causalities) {
            this.causalities = causalities;
            return this;
        }

        public Trait build() {
            NeuroTrait neuroTrait = new NeuroTrait();
            neuroTrait.setId(this.id);
            neuroTrait.setTrait(this.trait);
            neuroTrait.setExplanation(this.explanation);
            neuroTrait.setLevel(this.level);
            neuroTrait.setMaxPoints(this.maxPoints);
            neuroTrait.setCausalities(this.causalities);
            return neuroTrait;
        }


    }

}
