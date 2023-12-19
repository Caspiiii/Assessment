package at.ac.tuwien.sepr.groupphase.backend.integrationtest;


import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AnswerDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.TraitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AnswerMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.repository.AnswerRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class
QuestionEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserRepositoryInterface userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private AnswerMapper answerMapper;
    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();
        userRepository.save(APPLICATION_USER);
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);
        answerRepository.save(answer1);
        answerRepository.save(answer2);
    }

    private final ApplicationUser APPLICATION_USER = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
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
        .withApplicationUser(APPLICATION_USER)
        .withQuestion(question1)
        .build();
    private final Answer answer2 = Answer.AnswerBuilder.aDefaultAnswer()
        .withAnswer(3)
        .withApplicationUser(APPLICATION_USER)
        .withQuestion(question2)
        .build();
    String QUESTIONS_BASE_URI = BASE_URI + "/questions";

    @Test
    public void findQuestionsWithExistingLevelGetsQuestions() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(QUESTIONS_BASE_URI + "/" + 1)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<QuestionDto> questionDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            QuestionDto[].class));

        assertEquals(2, questionDtos.size());
    }

    @Test
    public void findQuestionsWithNonExistingLevelGetsEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(QUESTIONS_BASE_URI + "/" + 7)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<QuestionDto> questionDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            QuestionDto[].class));
        assertEquals(0, questionDtos.size());
    }


    @Test
    public void createValidAnswersReturnsAnswers() throws Exception {
        List<AnswerDto> answers = Arrays.asList(
            answerMapper.answerToAnswerDto(answer1),
            answerMapper.answerToAnswerDto(answer2)
        );
        String body = objectMapper.writeValueAsString(answers);
        MvcResult mvcResult = this.mockMvc.perform(post(QUESTIONS_BASE_URI + "/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<AnswerDto> actualAnswers = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(answers, actualAnswers)
        );
    }

    @Test
    public void createAnswersOfInvalidUserThrows404() throws Exception {
        APPLICATION_USER.setEmail("non@existing");
        answer1.setApplicationUser(APPLICATION_USER);
        List<AnswerDto> answers = Arrays.asList(
            answerMapper.answerToAnswerDto(answer1),
            answerMapper.answerToAnswerDto(answer2)
        );
        String body = objectMapper.writeValueAsString(answers);
        MvcResult mvcResult = this.mockMvc.perform(post(QUESTIONS_BASE_URI + "/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
            () -> assertEquals(response.getContentAsString(), "Could not find the user with the email address non@existing")
        );
    }

    @Test
    public void findAnswersForNonExistingQuestionReturnsEmptyString() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(QUESTIONS_BASE_URI + "/{questionId}/answers", 999)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();


        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
            () -> assertEquals("", response.getContentAsString())
        );
    }

    @Test
    public void createAnswerForNonExistingQuestionReturnsEmptyString() throws Exception {
        AnswerDto newAnswerDto = new AnswerDto();
        newAnswerDto.setAnswer(5);

        String body = objectMapper.writeValueAsString(newAnswerDto);
        MvcResult mvcResult = this.mockMvc.perform(post(QUESTIONS_BASE_URI + "/{questionId}/answers", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus()),
            () -> assertEquals("", response.getContentAsString())
        );
    }

    @Test
    public void createAnswerWithInvalidDataReturnsNotFound() throws Exception {
        AnswerDto newAnswerDto = new AnswerDto(); // Invalid as it does not set the 'answer' field.

        String body = objectMapper.writeValueAsString(newAnswerDto);
        MvcResult mvcResult = this.mockMvc.perform(post(QUESTIONS_BASE_URI + "/{questionId}/answers", question1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void getAssessmentForUserIfNotAllQuestionsAreAnsweredReturnsEmptyArray() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get(QUESTIONS_BASE_URI + "/assessments/{email}", APPLICATION_USER.getEmail())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<TraitDto> traits = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
            () -> assertNotNull(traits),
            () -> assertEquals(0, traits.size())
        );
    }

    @Test
    public void getAssessmentForNonExistingUserThrows404() throws Exception {
        String email = "non@existing.com";
        MvcResult mvcResult = mockMvc.perform(get(QUESTIONS_BASE_URI + "/assessments/{email}", email)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void findAllQuestionsForLevelReturnsQuestions() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(QUESTIONS_BASE_URI + "/{level}", 1)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<QuestionDto> questions = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
            () -> assertNotNull(questions),
            () -> assertEquals(2, questions.size())
        );
    }

    @Test
    public void findAllQuestionsForNonExistingLevelReturnsEmptyArray() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(QUESTIONS_BASE_URI + "/{level}", 5)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<QuestionDto> questions = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
            () -> assertNotNull(questions),
            () -> assertEquals(0, questions.size())
        );
    }

    @Test
    public void findAllQuestionsReturnsAllQuestions() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(QUESTIONS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<QuestionDto> questions = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
            () -> assertNotNull(questions),
            () -> assertEquals(3, questions.size())
        );
    }

    @Test
    public void getAnswersForUserGetsAnswers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(QUESTIONS_BASE_URI + "/answers/{userId}", TEST_USER_EMAIL)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<AnswerDto> answers = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll(
            () -> assertNotNull(answers),
            () -> assertEquals(2, answers.size())
        );
    }

    @Test
    public void getAnswersForInvalidUserThrows() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(QUESTIONS_BASE_URI + "/answers/{userId}", "non@existing")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
