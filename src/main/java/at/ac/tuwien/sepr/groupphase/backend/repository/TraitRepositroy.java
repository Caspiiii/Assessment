package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Trait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The TraiRepository stores als Traits. This includes Psycho-, Neuro- and Processtraits.
 */
@Repository
public interface TraitRepositroy extends JpaRepository<Trait, Long> {

    List<Trait> findAllByLevel(int level);
}
