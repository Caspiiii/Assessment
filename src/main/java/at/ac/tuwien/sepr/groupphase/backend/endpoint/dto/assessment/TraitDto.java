package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class TraitDto {
    @NotNull(message = "trait must not be null")
    @Size(min = 1, max = 100, message = "Trait must be between 1 and 100 characters")
    @NotBlank(message = "Trait must not be blank")
    private String trait;
    @NotNull(message = "trait must not be null")
    @Size(min = 1, max = 1000, message = "Trait explanation must be between 1 and 1000 characters")
    @NotBlank(message = "Trait must not be blank")
    private String explanation;

    @Max(30)
    @Max(0)
    @NotNull(message = "result must not be null")
    private int result;

    @Max(1)
    @Max(0)
    @NotNull(message = "result percentage must not be null")
    private double resultPercentage;

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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public double getResultPercentage() {
        return resultPercentage;
    }

    public void setResultPercentage(double resultPercentage) {
        this.resultPercentage = resultPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TraitDto traitDto = (TraitDto) o;
        return result == traitDto.result && Double.compare(resultPercentage, traitDto.resultPercentage) == 0 && Objects.equals(trait, traitDto.trait) && Objects.equals(explanation, traitDto.explanation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trait, explanation, result, resultPercentage);
    }

    @Override
    public String toString() {
        return "TraitDto{"
            + "trait='" + trait + '\''
            + ", explanation='" + explanation + '\''
            + ", result=" + result
            + ", resultPercentage=" + resultPercentage
            + '}';
    }

    public static final class TraitDtoBuilder {
        private String trait;
        private String explanation;
        private int result;
        private double resultPercentage;

        public TraitDtoBuilder() {
        }

        public static TraitDtoBuilder aProcessDto() {
            return new TraitDtoBuilder();
        }

        public TraitDtoBuilder withTrait(String trait) {
            this.trait = trait;
            return this;
        }

        public TraitDtoBuilder withExplanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public TraitDtoBuilder withResult(int result) {
            this.result = result;
            return this;
        }

        public TraitDtoBuilder withResultPercentage(double resultPercentage) {
            this.resultPercentage = resultPercentage;
            return this;
        }

        public TraitDto build() {
            ProcessTraitDto traitDto = new ProcessTraitDto();
            traitDto.setTrait(this.trait);
            traitDto.setExplanation(this.explanation);
            traitDto.setResult(this.result);
            traitDto.setResultPercentage(this.resultPercentage);
            return traitDto;
        }
    }
}
