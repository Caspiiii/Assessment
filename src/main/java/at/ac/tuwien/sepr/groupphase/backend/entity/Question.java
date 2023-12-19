package at.ac.tuwien.sepr.groupphase.backend.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Objects;

/**
 * Question entity with the question and the corresponding level. The possible levels are 1, 2 and 3.
 * 1 - For process-oriented
 * 2 - For neurological
 * 3 - For psychological/self
 */
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 1000)
    private String question;
    @Column(nullable = false, name = "level")
    @Min(1)
    @Max(3)
    private int level;

    @Column(nullable = false, name = "reverse")
    private boolean reverse = false;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "category")
    private Trait category;

    //Relation to answers
    @OneToMany(mappedBy = "question")
    private List<Answer> answers;

    public Question() {
    }

    public Question(String question, int level) {
        this.question = question;
        this.level = level;
    }

    public Question(String question, int level, Trait category) {
        this.question = question;
        this.level = level;
        this.category = category;
    }

    public Question(String question, int level, boolean reverse, Trait category) {
        this.question = question;
        this.level = level;
        this.reverse = reverse;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Question{" + "id=" + id
            + ", question='" + question + '\''
            + ", level=" + level
            + ", category=" + category
            + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Question question1 = (Question) o;
        return level == question1.level && Objects.equals(id, question1.id) && Objects.equals(question, question1.question) && Objects.equals(answers, question1.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, level, answers);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCategory(Trait category) {
        this.category = category;
    }

    public Trait getCategory() {
        return category;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public static class QuestionBuilder {
        private Long id;
        private String question;
        private int level;

        private boolean reverse = false;

        private List<Answer> answers;

        private QuestionBuilder() {
        }

        public static QuestionBuilder aDefaultQuestion() {
            return new QuestionBuilder();
        }

        public QuestionBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public QuestionBuilder withQuestion(String question) {
            this.question = question;
            return this;
        }

        public QuestionBuilder withLevel(int level) {
            this.level = level;
            return this;
        }

        public QuestionBuilder withReverse(boolean reverse) {
            this.reverse = reverse;
            return this;
        }

        public QuestionBuilder withAnswers(List<Answer> answers) {
            this.answers = answers;
            return this;
        }

        public Question build() {
            Question question = new Question();
            question.setId(this.id);
            question.setQuestion(this.question);
            question.setLevel(this.level);
            question.setAnswers(this.answers);
            question.setReverse(this.reverse);
            return question;
        }
    }
}
