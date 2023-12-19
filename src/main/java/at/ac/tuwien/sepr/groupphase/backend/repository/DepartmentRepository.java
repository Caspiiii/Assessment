package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query(value = "SELECT d FROM Department d WHERE d.manager.email = ?1")
    Optional<Department> findByManagerEmail(String email);

    Optional<Department> findById(Long id);

    List<Department> findAll();

    Optional<Department> findByName(String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ApplicationUser SET department_id = ?1 WHERE id = ?2", nativeQuery = true)
    void addUserToDepartment(Long departmentId, Long userId);


}
