package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Answer;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import at.ac.tuwien.sepr.groupphase.backend.repository.AnswerRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

//@Profile("withAnswers")
@Component
public class AnswerDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private AnswerRepository answerRepository;

    @Autowired
    QuestionDataGenerator questionDataGenerator;

    private static final String ADMIN_EMAIL = "admin@email.com";

    private static final String USER_EMAIL_1 = "caspian@email.com";


    private UserRepositoryInterface userRepository;

    private QuestionRepository questionRepository;

    public AnswerDataGenerator(AnswerRepository answerRepository, QuestionRepository questionRepository, UserRepositoryInterface userRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void generateAnswers() {
        List<Question> questions = questionRepository.findAll();
        Optional<ApplicationUser> user = userRepository.findByEmail(ADMIN_EMAIL);
        Optional<ApplicationUser> caspian = userRepository.findByEmail(USER_EMAIL_1);
        ApplicationUser user1 = null;
        ApplicationUser caspianUser = null;
        int min = 1;
        int max = 4;
        int[] answers = {3, 3, 3, 3, 4, 3, 2, 3, 2, 2, 2, 2, 2, 1, 3, 3, 4, 3, 2, 3, 4, 3, 3, 2, 2, 2, 3, 3, 3, 2, 4, 3, 4, 3, 3, 3,
            3, 2, 3, 2, 4, 1, 4, 2, 4, 4, 2, 1,
            1, 2, 4, 2, 3, 3, 1, 3, 4, 4, 2, 2, 2, 2, 3, 4, 3, 4, 3, 2, 1};
        if (user.isPresent()) {
            user1 = user.get();
        }
        if (caspian.isPresent()) {
            caspianUser = caspian.get();
        }
        int counter = 0;
        for (Question q : questions) {
            int answer = answers[counter++];
            int randomNum = (int) (Math.random() * (max - min + 1)) + min;
            answerRepository.save(new Answer(randomNum, q, user1));
            answerRepository.save(new Answer(answer, q, caspianUser));
        }

    }
}
