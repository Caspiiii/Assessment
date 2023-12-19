package at.ac.tuwien.sepr.groupphase.backend.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;


/**
 * Stores the answer to one question by one person. This is necesary because there is a n:m realtion between the entities Question and ApplicationUser
 */
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "answer")
    @Min(0)
    @Max(4)
    private int answer;
    //Foreign key to a question.
    @ManyToOne()
    @JoinColumn(name = "questionId")
    @NotNull(message = "question can not be null")
    private Question question;
    //Foreign key to a user.
    @ManyToOne()
    @JoinColumn(name = "applicationUser")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "Application can not be null")
    private ApplicationUser applicationUser;


    public Answer(int answer, Question question, ApplicationUser applicationUser) {
        this.answer = answer;
        this.question = question;
        this.applicationUser = applicationUser;
    }

    protected Answer() {
    }

    @Override
    public String toString() {
        return "Answer{"
            + "id=" + id
            + ", answer=" + answer
            + ", question=" + question
            + ", applicationUser=" + applicationUser
            + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Answer answer1 = (Answer) o;
        return Objects.equals(question, answer1.question) && Objects.equals(applicationUser, answer1.applicationUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, answer, question, applicationUser);
    }

    public static final class AnswerBuilder {
        private Long id;
        private int answer;
        private Question question;
        private ApplicationUser applicationUser;

        private AnswerBuilder() {
        }

        public static AnswerBuilder aDefaultAnswer() {
            return new AnswerBuilder();
        }

        public AnswerBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public AnswerBuilder withAnswer(int answer) {
            this.answer = answer;
            return this;
        }

        public AnswerBuilder withQuestion(Question question) {
            this.question = question;
            return this;
        }

        public AnswerBuilder withApplicationUser(ApplicationUser applicationUser) {
            this.applicationUser = applicationUser;
            return this;
        }

        public Answer build() {
            Answer answer = new Answer();
            answer.setId(this.id);
            answer.setAnswer(this.answer);
            answer.setQuestion(this.question);
            answer.setApplicationUser(this.applicationUser);
            return answer;
        }

    }

}
