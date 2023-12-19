package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(value = "PST")
public class PsychoTrait extends Trait {
    public PsychoTrait(String trait, String explanation, int level, int maxPoints) {
        super(trait, explanation, level, maxPoints);
    }

    public PsychoTrait() {
    }

    public static final class PsychoTraitBuilder {
        private Long id;
        private String trait;
        private String explanation;
        private int level;
        private int maxPoints;

        private PsychoTraitBuilder() {
        }

        public static PsychoTraitBuilder aPsychoTrait() {
            return new PsychoTraitBuilder();
        }

        public PsychoTraitBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public PsychoTraitBuilder withTrait(String trait) {
            this.trait = trait;
            return this;
        }

        public PsychoTraitBuilder withExplanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public PsychoTraitBuilder withLevel(int level) {
            this.level = level;
            return this;
        }

        public PsychoTraitBuilder withMaxPoints(int maxPoints) {
            this.maxPoints = maxPoints;
            return this;
        }

        public PsychoTrait build() {
            PsychoTrait trait = new PsychoTrait();
            trait.setId(this.id);
            trait.setTrait(this.trait);
            trait.setExplanation(this.explanation);
            trait.setLevel(this.level);
            trait.setMaxPoints(this.maxPoints);
            return trait;
        }


    }
}
