package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PsychoTraitDto extends TraitDto {
    public static final class PsychoTraitDtoBuilder {
        @NotNull(message = "trait must not be null")
        @Size(min = 1, max = 100, message = "Trait must be between 1 and 100 characters")
        @NotBlank(message = "Trait must not be blank")
        private String trait;
        @NotNull(message = "trait must not be null")
        @Size(min = 1, max = 1000, message = "Trait explanation must be between 1 and 1000 characters")
        @NotBlank(message = "Trait must not be blank")
        private String explanation;

        @Max(10)
        @Max(0)
        @NotNull(message = "result must not be null")
        private int result;
        @Max(1)
        @Max(0)
        @NotNull(message = "result percentage must not be null")
        private double resultPercentage;

        public PsychoTraitDtoBuilder() {
        }

        public static PsychoTraitDtoBuilder aPsychoDto() {
            return new PsychoTraitDtoBuilder();
        }

        public PsychoTraitDtoBuilder withTrait(String trait) {
            this.trait = trait;
            return this;
        }

        public PsychoTraitDtoBuilder withExplanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public PsychoTraitDtoBuilder withResult(int result) {
            this.result = result;
            return this;
        }

        public PsychoTraitDtoBuilder withResultPercentage(double resultPercentage) {
            this.resultPercentage = resultPercentage;
            return this;
        }

        public PsychoTraitDto build() {
            PsychoTraitDto traitDto = new PsychoTraitDto();
            traitDto.setTrait(this.trait);
            traitDto.setExplanation(this.explanation);
            traitDto.setResult(this.result);
            traitDto.setResultPercentage(this.resultPercentage);
            return traitDto;
        }
    }


}
