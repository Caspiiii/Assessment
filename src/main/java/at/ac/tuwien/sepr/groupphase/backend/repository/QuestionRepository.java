package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Stores all Questions.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByLevel(int level);
}
