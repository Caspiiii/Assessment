package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AnswerDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.TraitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AnswerMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.QuestionMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import at.ac.tuwien.sepr.groupphase.backend.service.QuestionService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/questions")
public class QuestionEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private QuestionService questionService;
    private QuestionMapper questionMapper;

    private UserService userService;

    private AnswerMapper answerMapper;

    public QuestionEndpoint(QuestionService questionService, QuestionMapper questionMapper, AnswerMapper answerMapper, UserService userService) {
        this.questionService = questionService;
        this.questionMapper = questionMapper;
        this.answerMapper = answerMapper;
        this.userService = userService;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new question", security = @SecurityRequirement(name = "apiKey"))
    public QuestionDto create(@Valid @RequestBody QuestionDto questionDto) {
        LOGGER.info("POST /api/v1/question body: {}", questionDto);
        Question created = questionService.addQuestion(questionMapper.questionDtoToQuestion(questionDto));
        return questionMapper.questionToQuestionDto(created);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/answers")
    @Operation(summary = "Create new answers", security = @SecurityRequirement(name = "apiKey"))
    public List<AnswerDto> create(@Valid @RequestBody List<AnswerDto> answers) {
        LOGGER.info("POST /api/v1/question/answers body: {}", answers);
        ApplicationUser applicationUser = userService.findUserByEmail(answers.get(0).getEmailUser());
        List<Answer> answersEntity = answerMapper.answerDtoToAnswer(answers);
        for (Answer a : answersEntity) {
            a.setApplicationUser(applicationUser);
        }
        List<Answer> createdAnswers = questionService.createAnswers(answersEntity);
        List<AnswerDto> answerDtos = answerMapper.answerToAnswerDto(createdAnswers);
        LOGGER.info(answerDtos.toString());
        return answerDtos;
    }

    @PermitAll
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/assessments/{email}")
    @Operation(summary = "Get assessment", security = @SecurityRequirement(name = "apiKey"))
    public List<TraitDto> getAssessment(@PathVariable String email) {
        LOGGER.info("Get the assessment for single person");
        return this.questionService.getAssessment(email);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{level}")
    @Operation(summary = "Get questionnaire with specific level", security = @SecurityRequirement(name = "apiKey"))
    public List<QuestionDto> find(@PathVariable int level) {
        LOGGER.info("GET /api/v1/questions/level {}", level);
        List<Question> questionnaire = questionService.getQuestionnaire(level);
        LOGGER.info("Questions with level {}: {}", level, questionnaire.toString());
        List<QuestionDto> listDto = questionMapper.questionToQuestionDto(questionnaire);
        return listDto;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    @Operation(summary = "Get questionnaire with non-specific level", security = @SecurityRequirement(name = "apiKey"))
    public List<QuestionDto> find() {
        List<Question> questionnaire = questionService.allQuestions();
        LOGGER.info("Questions: {}", questionnaire.toString());
        List<QuestionDto> listDto = questionMapper.questionToQuestionDto(questionnaire);
        return listDto;
    }


    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/answers/{userId}")
    @Operation(summary = "Get answers for a user", security = @SecurityRequirement(name = "apiKey"))
    public List<AnswerDto> getAnswersForUser(@PathVariable String userId) {
        LOGGER.info("GET /api/v1/questions/answers/{}", userId);
        ApplicationUser applicationUser = userService.findUserByEmail(userId);
        List<Answer> userAnswers = questionService.getAnswersForUser(applicationUser);
        List<AnswerDto> answerDtos = answerMapper.answerToAnswerDto(userAnswers);
        LOGGER.info(answerDtos.toString());
        return answerDtos;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/answers/{userId}")
    @Operation(summary = "Delete answers for a user", security = @SecurityRequirement(name = "apiKey"))
    public void deleteAnswersForUser(@PathVariable String userId) {
        LOGGER.info("DELETE /api/v1/questions/answers/{}", userId);

        LOGGER.info("Answers deleted for user: {}", userId);
    }

}
