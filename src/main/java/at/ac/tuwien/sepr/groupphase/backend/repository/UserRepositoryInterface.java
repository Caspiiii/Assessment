package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepositoryInterface extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByEmail(String email);

    Optional<ApplicationUser> findById(Long id);

    @Query("SELECT a FROM ApplicationUser a "
        + "WHERE ((LOWER(a.firstName) LIKE LOWER(concat('%', :firstInput, '%'))) AND (LOWER(a.lastName) LIKE LOWER(concat('%', :lastInput, '%')))) "
        + "OR ((LOWER(a.lastName) LIKE LOWER(concat('%', :firstInput, '%'))) AND (LOWER(a.firstName) LIKE LOWER(concat('%', :lastInput, '%'))))")
    List<ApplicationUser> findByName(@Param("firstInput") String firstInput,
                                     @Param("lastInput") String lastInput,
                                     Pageable pageable);

    @Query("SELECT a FROM ApplicationUser a "
        + "WHERE (((LOWER(a.firstName) LIKE LOWER(concat('%', :firstInput, '%'))) AND (LOWER(a.lastName) LIKE LOWER(concat('%', :lastInput, '%')))) "
        + "OR ((LOWER(a.lastName) LIKE LOWER(concat('%', :firstInput, '%'))) AND (LOWER(a.firstName) LIKE LOWER(concat('%', :lastInput, '%')))))"
        + "AND a.department = :department "
        + "AND (a NOT IN ("
        + "SELECT p.members"
        + "    FROM Project p"
        + "    WHERE p.id = :projectId))")
    List<ApplicationUser> findSuggestionsByName(@Param("firstInput") String firstInput,
                                                @Param("lastInput") String lastInput,
                                                @Param("projectId") Integer projectId,
                                                @Param("department") Department department,
                                                Pageable pageable);

    List<ApplicationUser> findAll();

    List<ApplicationUser> findAllByEmailIn(List<String> emails);
}
