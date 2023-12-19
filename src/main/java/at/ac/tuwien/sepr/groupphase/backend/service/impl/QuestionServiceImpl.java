package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.NeuroTraitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.ProcessTraitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.PsychoTraitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.TraitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AnswerRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.QuestionService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.algorithms.SingleAssessment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    private final AnswerRepository answerRepository;

    private final SingleAssessment singleAssessment;

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public QuestionServiceImpl(QuestionRepository questionRepository, AnswerRepository answerRepository, SingleAssessment singleAssessment, UserService userService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.singleAssessment = singleAssessment;
        this.userService = userService;
    }

    @Transactional
    @Override
    public Question addQuestion(Question question) {
        LOGGER.trace("Added Question in {}: {}", getClass().getName(), question.toString());
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getQuestionnaire(int level) {
        LOGGER.trace("got Questionnaire in {} with level : {}", getClass().getName(), level);
        return questionRepository.findAllByLevel(level);
    }

    @Transactional
    @Override
    public List<Answer> createAnswers(List<Answer> answers) {
        LOGGER.trace("created Answers in {}", getClass().getName());
        LOGGER.info("answers size: {}", answers.size());
        List<Answer> answersOld = answerRepository.findAll();
        if (!answersOld.isEmpty()) {
            for (Answer answer : answers) {
                boolean isAlreadySet = false;
                for (Answer answerOld : answersOld) {
                    if (answerOld.getQuestion().getId().equals(answer.getQuestion().getId())
                        && answerOld.getApplicationUser().getEmail().equals(answer.getApplicationUser().getEmail())) {
                        answerOld.setAnswer(answer.getAnswer());
                        answerRepository.save(answerOld);
                        isAlreadySet = true;
                        break;
                    }
                }
                if (!isAlreadySet) {
                    answerRepository.save(answer);
                }
            }
        }
        return answers;

    }

    public List<Answer> getAnswersForUser(ApplicationUser user) {
        LOGGER.trace("get Answers for user {}", user);
        return answerRepository.findAllByApplicationUser(user);
    }


    @Override
    public Question getQuestion(Long id) {
        LOGGER.trace("get questions for user {}", id);
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        }
        throw new NotFoundException();
    }

    @Override
    public List<Question> allQuestions() {
        LOGGER.trace("get all questions ");
        return questionRepository.findAll();
    }

    @Override
    public List<TraitDto> getAssessment(String email) {
        LOGGER.trace("get getAssessment for user {}", email);
        ApplicationUser user = userService.findUserByEmail(email);
        List<Answer> answers = getAnswersForUser(userService.findUserByEmail(email));
        List<Answer> notAnsweredQuestions = answers.stream()
            .filter(answer -> answer.getAnswer() == 0)
            .toList();
        List<TraitDto> wholeList = new ArrayList<>();
        if (notAnsweredQuestions.isEmpty()) {
            List<ProcessTraitDto> list1 = singleAssessment.calculateProcessTraits(user);
            List<NeuroTraitDto> list2 = singleAssessment.calculateNeuroTraits(user);
            List<PsychoTraitDto> list3 = singleAssessment.calculatePsychoTraits(user);
            wholeList.addAll(list1);
            wholeList.addAll(list2);
            wholeList.addAll(list3);
        }
        return wholeList;
    }


}
