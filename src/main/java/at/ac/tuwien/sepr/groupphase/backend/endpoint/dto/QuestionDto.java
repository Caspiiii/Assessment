package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class QuestionDto {
    Long id;
    @NotNull(message = "Question must not be null")
    @Size(min = 1, max = 1000, message = "Email must be between 1 and 100 characters")
    @NotBlank(message = "Question must not be empty")
    private String question;
    @Max(3)
    @Min(1)
    private int level;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuestionDto that = (QuestionDto) o;
        return level == that.level && Objects.equals(question, that.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, level);
    }

    @Override
    public String toString() {
        return "QuestionDto{"
            + "question='" + question + '\''
            + ", level=" + level
            + '}';
    }

    public static final class QuestionDtoBuilder {
        private Long id;
        private String question;
        private int level;

        public QuestionDtoBuilder() {
        }

        public static QuestionDtoBuilder aQuestionDto() {
            return new QuestionDtoBuilder();
        }

        public QuestionDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public QuestionDtoBuilder withQuestion(String question) {
            this.question = question;
            return this;
        }

        public QuestionDtoBuilder withLevel(int level) {
            this.level = level;
            return this;
        }

        public QuestionDto build() {
            QuestionDto questionDto = new QuestionDto();
            questionDto.setId(id);
            questionDto.setQuestion(question);
            questionDto.setLevel(level);
            return questionDto;
        }
    }
}
