package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.assessment.TraitDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;

import java.util.List;

/**
 * Service Interface for accessing and adding questions to the questionnaire. This Service class is not used for assessment algorithms but only for
 * the questions themselves
 */
public interface QuestionService {

    /**
     * adds  a given question to the questionnaire as an entity. Attention: This function does not take DTOs but entities
     *
     * @param question the question to be added to the questionnaire
     */
    Question addQuestion(Question question);

    /**
     * returns all questions for the given level.
     *
     * @param level the level
     * @return returns all questions as a list
     */
    List<Question> getQuestionnaire(int level);

    /**
     * creates answers for all questions that have been answered.
     *
     * @param answers the answers
     * @return the created list of answers.
     */
    List<Answer> createAnswers(List<Answer> answers);

    /**
     * Get answers for a specific user.
     *
     * @param user the user for whom answers are requested
     * @return the list of answers for the specified user
     */
    List<Answer> getAnswersForUser(ApplicationUser user);


    /**
     * Get question by Id. Throws NotFoundException if there is no question with that Id.
     *
     * @return The Question for the Id.
     */
    Question getQuestion(Long id);

    /**
     * Get all questions.
     *
     * @return all questions
     */
    List<Question> allQuestions();

    /**
     * Get the assessment for Person with email. Only if all questions are answered, the Assessment is executed.
     * This is because the Algorithm does only work with a complete questionnaire.
     *
     * @return All analysed traits
     */
    List<TraitDto> getAssessment(String email);
}
