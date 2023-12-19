package at.ac.tuwien.sepr.groupphase.backend.unittests;


import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AnswerRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class QuestionServiceTest implements TestData {

    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserRepositoryInterface userRepository;
    @Autowired
    private QuestionService service;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    private ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(-1L)
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.USER)
        .build();

    private final Question question1 = Question.QuestionBuilder.aDefaultQuestion()
        .withQuestion("Question 1")
        .withLevel(1)
        .build();

    private final Question question2 = Question.QuestionBuilder.aDefaultQuestion()
        .withQuestion("Question 2")
        .withLevel(1)
        .build();
    private final Question question3 = Question.QuestionBuilder.aDefaultQuestion()
        .withQuestion("Question 3")
        .withLevel(2)
        .build();

    private final Answer answer1 = Answer.AnswerBuilder.aDefaultAnswer()
        .withAnswer(1)
        .withApplicationUser(applicationUser)
        .withQuestion(question1)
        .build();
    private final Answer answer2 = Answer.AnswerBuilder.aDefaultAnswer()
        .withAnswer(3)
        .withApplicationUser(applicationUser)
        .withQuestion(question2)
        .build();

    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();
        List<Answer> answers = new ArrayList<>();
        answers.add(answer1);
        answers.add(answer2);
        applicationUser.setAnswers(answers);
        userRepository.save(applicationUser);
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);
    }


    @Test
    public void gettingQuestionWithValidIdGetsQuestion() {

        long id = (questionRepository.findAll().get(0)).getId();
        Question q = service.getQuestion(id);
        assertAll(
            () -> {
                assertNotNull(q);
                assertEquals((questionRepository.findAll().get(0)).getLevel(), q.getLevel());
                assertEquals((questionRepository.findAll().get(0)).getQuestion(), q.getQuestion());
            }
        );

    }

    @Test
    public void gettingQuestionWithInvalidIdThrows404() {
        assertThrows(NotFoundException.class, () -> {
            service.getQuestion(-1L);
        });
    }

    @Test
    public void createAnswersSavesAnswersInDatabase() {
        List<Answer> answers = new LinkedList<>();
        answers.add(answer1);
        answers.add(answer2);
        List<Answer> createdAnswers = service.createAnswers(answers);
        assertAll(
            () -> assertNotNull(createdAnswers),
            () -> assertEquals(2, createdAnswers.size())
        );
    }
    @Test
    public void getAnswersForUserGetsAnswers() {
        answerRepository.save(answer1);
        answerRepository.save(answer2);
        List<Answer> answers = service.getAnswersForUser(applicationUser);
        assertAll(
            () -> assertNotNull(answers),
            () -> assertEquals(2, answers.size())
        );
    }
    @Test
    public void getAnswersIfNoAnswersExistForUserReturnsEmptyArray() {
        List<Answer> answers = service.getAnswersForUser(applicationUser);
        assertAll(
            () -> assertNotNull(answers),
            () -> assertEquals(0, answers.size())
        );
    }
    @Test
    public void gettingAllQuestionsGetsAllQuestions() {
        List<Question> questionList = service.allQuestions();
        assertAll(
            () -> {
                assertNotNull(questionList);
                assertEquals(3, questionList.size());
                assertEquals(1, questionList.get(0).getLevel());
                assertEquals("Question 1", questionList.get(0).getQuestion());
                assertEquals(1, questionList.get(1).getLevel());
                assertEquals("Question 2", questionList.get(1).getQuestion());
                assertEquals(2, questionList.get(2).getLevel());
                assertEquals("Question 3", questionList.get(2).getQuestion());
            }
        );
    }

    @Test
    public void gettingQuestionnaireGetsAllQuestionsOfQuestionnaireWithGivenLevel() {
        List<Question> questionList = service.getQuestionnaire(1);
        assertAll(
            () -> {
                assertNotNull(questionList);
                assertEquals(2, questionList.size());
                assertEquals(1, questionList.get(0).getLevel());
                assertEquals("Question 1", questionList.get(0).getQuestion());
                assertEquals(1, questionList.get(1).getLevel());
                assertEquals("Question 2", questionList.get(1).getQuestion());
            }
        );
    }

    @Test
    public void gettingQuestionnaireWithInvalidLevelReturnsEmptyList() {
        List<Question> questionList = service.getQuestionnaire(5);
        assertAll(
            () -> assertNotNull(questionList),
            () -> assertEquals(0, questionList.size())

        );
    }


    @Test
    public void savingAnswerSavesAnswerInDatabase() {
        List<Answer> answerList = new LinkedList<>();
        answerList.add(answer1);
        answerList.add(answer2);
        List<Answer> savedAnswers = service.createAnswers(answerList);
        assertAll(
            () -> {
                assertEquals(answerList.size(), savedAnswers.size());
                assertEquals(answer1, answerList.get(0));
                assertEquals(answer2, answerList.get(1));
            });
    }

}
