package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.QuestionMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class QuestionMapperTest implements TestData {

    private final Question question1 = Question.QuestionBuilder.aDefaultQuestion()
        .withQuestion("Question 1")
        .withLevel(1)
        .withId(1L)
        .build();

    private final Question question2 = Question.QuestionBuilder.aDefaultQuestion()
        .withQuestion("Question 2")
        .withLevel(1)
        .withId(2L)
        .build();


    @Autowired
    private QuestionMapper questionMapper;

    @Test
    public void givenNothing_whenMapListOfAnswerDtosToEntity_thenEntitiesHaveAllProperties() {
        List<Question> questionList = new LinkedList<>();
        questionList.add(question1);
        questionList.add(question2);
        List<QuestionDto> questionDtoList = questionMapper.questionToQuestionDto(questionList);
        assertAll(
            () -> assertEquals(1L, questionDtoList.get(0).getId()),
            () -> assertEquals(question1.getQuestion(), questionDtoList.get(0).getQuestion()),
            () -> assertEquals(1, questionDtoList.get(0).getLevel()),
            () -> assertEquals(2L, questionDtoList.get(1).getId()),
            () -> assertEquals(question2.getQuestion(), questionDtoList.get(1).getQuestion()),
            () -> assertEquals(1, questionDtoList.get(1).getLevel())
        );
    }

    @Test
    public void givenNothing_whenMapAnswerDtoToEntity_thenEntityHasAllProperties() {
        QuestionDto questionDto = questionMapper.questionToQuestionDto(question1);
        assertAll(
            () -> assertEquals(1L, questionDto.getId()),
            () -> assertEquals(question1.getQuestion(), questionDto.getQuestion()),
            () -> assertEquals(1, questionDto.getLevel())
        );
    }

}
