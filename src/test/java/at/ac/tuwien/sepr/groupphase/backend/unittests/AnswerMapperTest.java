package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AnswerDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AnswerMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class AnswerMapperTest implements TestData {

    private ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(1L)
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.USER)
        .build();
    private final Question question1 = Question.QuestionBuilder.aDefaultQuestion()
        .withQuestion("Question 1")
        .withLevel(1)
        .withId(1L)
        .build();

    private final Question question2 = Question.QuestionBuilder.aDefaultQuestion()
        .withQuestion("Question 2")
        .withLevel(1)
        .withId(1L)
        .build();
    private final Answer answer1 = Answer.AnswerBuilder.aDefaultAnswer()
        .withId(1L)
        .withAnswer(1)
        .withApplicationUser(applicationUser)
        .withQuestion(question1)
        .build();
    private final Answer answer2 = Answer.AnswerBuilder.aDefaultAnswer()
        .withId(1L)
        .withAnswer(3)
        .withApplicationUser(applicationUser)
        .withQuestion(question2)
        .build();
    private AnswerDto answerDto = new AnswerDto();

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private DatabaseCleaner databaseCleaner;


    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();
        List<Answer> l= new ArrayList<>();
        l.add(answer1);
        l.add(answer2);
        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .withRole(Role.ADMIN)
            .withAnswers(l)
            .build();
    }

    @Test
    public void givenNothing_whenMapListOfAnswerDtosToEntity_thenEntitiesHaveAllProperties() {
        List<Answer> answerList = new LinkedList<>();
        answerList.add(answer1);
        answerList.add(answer2);
        List<AnswerDto> answerDto = answerMapper.answerToAnswerDto(answerList);
        assertAll(
            () -> assertEquals(TEST_USER_EMAIL, answerDto.get(0).getEmailUser()),
            () -> assertEquals(answerMapper.questionToQuestionDto(question1), answerDto.get(0).getQuestion()),
            () -> assertEquals(1, answerDto.get(0).getAnswer()),
            () -> assertEquals(TEST_USER_EMAIL, answerDto.get(1).getEmailUser()),
            () -> assertEquals(answerMapper.questionToQuestionDto(question2), answerDto.get(1).getQuestion()),
            () -> assertEquals(3, answerDto.get(1).getAnswer())
        );
    }

    @Test
    public void givenNothing_whenMapQuestionDtoToEntity_thenEntityHasAllProperties() {
        QuestionDto questionDto= answerMapper.questionToQuestionDto(question1);
        assertAll(
            () -> assertEquals("Question 1", questionDto.getQuestion()),
            () -> assertEquals(1, questionDto.getLevel())
        );
    }

    @Test
    public void givenNothing_whenMapAnswerDtoToEntity_thenEntityHasAllProperties() {
        AnswerDto answerDto = answerMapper.answerToAnswerDto(answer1);
        assertAll(
            () -> assertEquals(TEST_USER_EMAIL, answerDto.getEmailUser()),
            () -> assertEquals(answerMapper.questionToQuestionDto(question1), answerDto.getQuestion()),
            () -> assertEquals(1, answerDto.getAnswer())
        );
    }

    @Test
    public void givenNothing_whenMapEntityToAnswerDto_thenDtoHasAllProperties() {
        answerDto.setAnswer(1);
        answerDto.setEmailUser(TEST_USER_EMAIL);
        answerDto.setQuestion(answerMapper.questionToQuestionDto(question1));
        List<AnswerDto> l = new ArrayList<>();
        l.add(answerDto);
        List<Answer> answers = answerMapper.answerDtoToAnswer(l);

        assertAll(
            () -> assertEquals(1, answers.size()),
            () -> assertEquals(answerDto.getAnswer(), answers.get(0).getAnswer()),
            () -> assertEquals(question1, answers.get(0).getQuestion())
        );
    }

    @Test
    public void givenEmptyList_whenMapListOfAnswerDtosToEntity_thenEmptyListReturned() {
        List<Answer> answerList = new ArrayList<>();
        List<AnswerDto> answerDtoList = answerMapper.answerToAnswerDto(answerList);
        assertTrue(answerDtoList.isEmpty());
    }

    @Test
    public void givenNullAnswerList_whenMapListOfAnswerDtosToEntity_thenAnswerListIsNull() {
        List<AnswerDto> nullAnswerDtoList = null;
        List<Answer> answerList = answerMapper.answerDtoToAnswer(nullAnswerDtoList);
        assertNull(answerList);
    }
}
