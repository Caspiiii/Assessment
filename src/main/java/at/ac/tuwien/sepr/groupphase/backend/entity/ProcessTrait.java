package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
@DiscriminatorValue(value = "PT")
public class ProcessTrait extends Trait {


    @ManyToMany(fetch = FetchType.EAGER)
    //@Cascade({CascadeType.DELETE})
    @JoinColumn(name = "regression_dep")
    List<PsychoTrait> regressionDependencies;


    public ProcessTrait(String trait, String explanation, int level, int maxPoints, List<PsychoTrait> regressionDependencies) {
        super(trait, explanation, level, maxPoints);
        this.regressionDependencies = regressionDependencies;
    }

    public ProcessTrait() {

    }


    public List<PsychoTrait> getRegressionDependencies() {
        return regressionDependencies;
    }

    public void setRegressionDependencies(List<PsychoTrait> regressionDependencies) {
        this.regressionDependencies = regressionDependencies;
    }

    public static final class ProcessTraitBuilder {
        private Long id;
        private String trait;
        private String explanation;
        private int level;
        private int maxPoints;

        private List<PsychoTrait> regressionDependencies;

        private ProcessTraitBuilder() {
        }

        public static ProcessTraitBuilder aPsychoTrait() {
            return new ProcessTraitBuilder();
        }

        public ProcessTraitBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ProcessTraitBuilder withTrait(String trait) {
            this.trait = trait;
            return this;
        }

        public ProcessTraitBuilder withExplanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public ProcessTraitBuilder withLevel(int level) {
            this.level = level;
            return this;
        }

        public ProcessTraitBuilder withMaxPoints(int maxPoints) {
            this.maxPoints = maxPoints;
            return this;
        }

        public ProcessTraitBuilder withRegressionDependencies(List<PsychoTrait> regressionDependencies) {
            this.regressionDependencies = regressionDependencies;
            return this;
        }

        public ProcessTrait build() {
            ProcessTrait trait = new ProcessTrait();
            trait.setId(this.id);
            trait.setTrait(this.trait);
            trait.setExplanation(this.explanation);
            trait.setLevel(this.level);
            trait.setMaxPoints(this.maxPoints);
            trait.setRegressionDependencies(this.regressionDependencies);
            return trait;
        }


    }
}
