package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class AnswerDto {

    @NotNull(message = "User must not be null")
    @Email
    @Size(min = 1, max = 100, message = "Email must be between 1 and 100 characters")
    @NotBlank(message = "User must not be blank")
    private String emailUser;
    @NotNull(message = "Question must not be null")
    private QuestionDto question;
    @NotNull(message = "Answer must not be null")
    @Max(4)
    @Min(0)
    private int answer;

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }


    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnswerDto answerDto = (AnswerDto) o;
        return answer == answerDto.answer && Objects.equals(emailUser, answerDto.emailUser) && Objects.equals(question, answerDto.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailUser, question, answer);
    }

    @Override
    public String toString() {
        return "AnswerDto{"
            + "emailUser='" + emailUser + '\''
            + ", question=" + question
            + ", answer=" + answer
            + '}';
    }

    public static final class AnswerDtoBuilder {
        private String emailUser;
        private QuestionDto question;
        private int answer;

        public AnswerDtoBuilder() {
        }

        public static AnswerDtoBuilder aQuestionDto() {
            return new AnswerDtoBuilder();
        }

        public AnswerDtoBuilder withQuestion(QuestionDto question) {
            this.question = question;
            return this;
        }

        public AnswerDtoBuilder withAnswer(int answer) {
            this.answer = answer;
            return this;
        }

        public AnswerDtoBuilder withLevel(String emailUser) {
            this.emailUser = emailUser;
            return this;
        }

        public AnswerDto build() {
            AnswerDto answerDto = new AnswerDto();
            answerDto.setAnswer(answer);
            answerDto.setEmailUser(emailUser);
            answerDto.setQuestion(question);
            return answerDto;
        }
    }
}
