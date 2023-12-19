package at.ac.tuwien.sepr.groupphase.backend.service.algorithms;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.NeuroTraitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.ProcessTraitDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.PsychoTraitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import at.ac.tuwien.sepr.groupphase.backend.entity.Trait;
import at.ac.tuwien.sepr.groupphase.backend.repository.AnswerRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TraitRepositroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Calculates the results of the Single assessment. There are 3 functions who calculate the Psycho-, Neuro- and Processtraits. The Algorithms are based
 * on the model of Julius Kuhl and the working process of Mastering Emotions.
 */
@Service
public class SingleAssessment {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private AnswerRepository answerRepository;
    private QuestionRepository questionRepository;

    private TraitRepositroy traitRepositroy;

    private ProcessTraitDto.ProcessTraitDtoBuilder processTraitDtoBuilder;

    private NeuroTraitDto.NeuroTraitDtoBuilder neuroTraitDtoBuilder;

    private PsychoTraitDto.PsychoTraitDtoBuilder psychoTraitDtoBuilder;

    public SingleAssessment(AnswerRepository answerRepository, QuestionRepository questionRepository, TraitRepositroy traitRepositroy) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.traitRepositroy = traitRepositroy;
        processTraitDtoBuilder = new ProcessTraitDto.ProcessTraitDtoBuilder();
        neuroTraitDtoBuilder = new NeuroTraitDto.NeuroTraitDtoBuilder();
        psychoTraitDtoBuilder = new PsychoTraitDto.PsychoTraitDtoBuilder();

    }


    /**
     * Calculate Processtraits. The function uses the trait-, question- and answerRepositorys. Runtime is in O(n*m) with n being the amount of traits
     * and m being the amount of questions.
     *
     * @param applicationUser the user for which the assessment shall be calculated for.
     * @return the calculated assessment. (In percent and absolute)
     */
    public List<ProcessTraitDto> calculateProcessTraits(ApplicationUser applicationUser) {
        List<ProcessTraitDto> processTraitDtos = new LinkedList<>();
        List<Trait> traits = traitRepositroy.findAllByLevel(1);
        List<Question> questions = questionRepository.findAllByLevel(1);
        List<Answer> answers = answerRepository.findAllByApplicationUser(applicationUser);
        if (answers.isEmpty()) {
            return new LinkedList<>();
        }
        Map<Question, Answer> questionAnswerMap = getQuestionAnswerMap(questions, answers);
        int total = 0;
        for (Trait t : traits) {
            int result = 0;
            processTraitDtoBuilder.withTrait(t.getTrait());
            processTraitDtoBuilder.withExplanation(t.getExplanation());
            for (Question q : questions) {
                Trait a = q.getCategory();
                Long test = q.getCategory().getId();
                Long test2 = t.getId();
                if (test.equals(test2)) {
                    Answer answer = questionAnswerMap.get(q);
                    if (answer != null) {
                        result += mapResult(answer.getAnswer());
                    }
                }
            }
            total += result;
            processTraitDtoBuilder.withResult(result);
            processTraitDtoBuilder.withResultPercentage(0);
            processTraitDtos.add(processTraitDtoBuilder.build());
        }

        for (ProcessTraitDto p : processTraitDtos) {
            p.setResultPercentage((double) p.getResult() / total);
        }

        return processTraitDtos;
    }

    /**
     * Calculate Neurotraits. The function uses the trait-, question- and answerRepositorys. Runtime is in O(n*m) with n being the amount of traits
     * and m being the amount of questions.
     *
     * @param applicationUser the user for which the assessment shall be calculated for.
     * @return the calculated assessment. (In percent and absolute)
     */
    public List<NeuroTraitDto> calculateNeuroTraits(ApplicationUser applicationUser) {
        List<NeuroTraitDto> neuroTraitDtos = new LinkedList<>();
        List<Trait> traits = traitRepositroy.findAllByLevel(2);
        List<Question> questions = questionRepository.findAllByLevel(2);
        List<Answer> answers = answerRepository.findAllByApplicationUser(applicationUser);
        if (answers.isEmpty()) {
            return new LinkedList<>();
        }
        Map<Question, Answer> questionAnswerMap = getQuestionAnswerMap(questions, answers);
        int total = 0;
        for (Trait t : traits) {
            int result = 0;
            neuroTraitDtoBuilder.withTrait(t.getTrait());
            neuroTraitDtoBuilder.withExplanation(t.getExplanation());
            for (Question q : questions) {
                if (q.getCategory().getId().equals(t.getId())) {
                    Answer answer = questionAnswerMap.get(q);
                    if (answer != null) {
                        result += (answer.getAnswer() - 1);
                    }
                }
            }
            total += result;
            neuroTraitDtoBuilder.withResult(result);
            neuroTraitDtoBuilder.withResultPercentage(0);
            neuroTraitDtos.add(neuroTraitDtoBuilder.build());
        }

        for (NeuroTraitDto p : neuroTraitDtos) {
            p.setResultPercentage((double) p.getResult() / total);
        }

        return neuroTraitDtos;
    }

    /**
     * Calculate Neurotraits. The function uses the trait-, question- and answerRepositorys. Runtime is in O(n*m) with n being the amount of traits
     * and m being the amount of questions.
     *
     * @param applicationUser the user for which the assessment shall be calculated for.
     * @return the calculated assessment. (In percent and absolute)
     */
    public List<PsychoTraitDto> calculatePsychoTraits(ApplicationUser applicationUser) {
        List<PsychoTraitDto> psychoTraitDtos = new LinkedList<>();
        List<Trait> traits = traitRepositroy.findAllByLevel(3);
        List<Question> questions = questionRepository.findAllByLevel(3);
        List<Answer> answers = answerRepository.findAllByApplicationUser(applicationUser);
        if (answers.isEmpty()) {
            return new LinkedList<>();
        }
        Map<Question, Answer> questionAnswerMap = getQuestionAnswerMap(questions, answers);
        int total = 0;
        for (Trait t : traits) {
            int result = 0;
            psychoTraitDtoBuilder.withTrait(t.getTrait());
            psychoTraitDtoBuilder.withExplanation(t.getExplanation());
            for (Question q : questions) {
                if (q.getCategory().getId().equals(t.getId())) {
                    Answer answer = questionAnswerMap.get(q);
                    if (answer != null) {
                        LOGGER.info("answer is: {} with reversed being {} and question {}", answer.getAnswer(), q.isReverse(), q.getCategory());
                        if (q.isReverse()) {
                            result += Math.abs((answer.getAnswer() - 4));
                        } else {
                            result += (answer.getAnswer() - 1);
                        }
                    }
                }
            }
            total += result;
            psychoTraitDtoBuilder.withResult(result);
            psychoTraitDtoBuilder.withResultPercentage(0);
            psychoTraitDtos.add(psychoTraitDtoBuilder.build());
        }

        for (PsychoTraitDto p : psychoTraitDtos) {
            p.setResultPercentage((double) p.getResult() / total);
        }

        return psychoTraitDtos;
    }

    private Map<Question, Answer> getQuestionAnswerMap(List<Question> questions, List<Answer> answers) {
        Map<Question, Answer> map = new HashMap<>();
        for (Question q : questions) {
            for (Answer a : answers) {
                if (a.getQuestion().getId().equals(q.getId())) {
                    map.put(q, a);
                    break;
                }
            }
        }
        return map;
    }

    private int mapResult(int unmappedResult) {
        if (unmappedResult == 1) {
            return 0;
        }
        if (unmappedResult == 2) {
            return 1;
        }
        if (unmappedResult == 3) {
            return 3;
        }
        return 5;

    }

}
