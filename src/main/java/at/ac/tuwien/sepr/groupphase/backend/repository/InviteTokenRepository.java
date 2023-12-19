package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.DepartmentInviteToken;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface InviteTokenRepository extends JpaRepository<DepartmentInviteToken, String> {
    @Query("SELECT r.email FROM DepartmentInviteToken r WHERE r.token = :token")
    String findEmailByToken(@Param("token") String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM DepartmentInviteToken r WHERE r.token = :token")
    void deleteByToken(@Param("token") String token);

    @Query("SELECT r FROM DepartmentInviteToken r WHERE r.manager = :manager")
    Optional<DepartmentInviteToken> findByManager(@Param("manager") String manager);

    @Query("SELECT r FROM DepartmentInviteToken r WHERE r.manager = :manager AND r.email = :email")
    Optional<DepartmentInviteToken> findByManagerAndEmail(@Param("manager") String manager, @Param("email") String email);

    @Query("SELECT r FROM DepartmentInviteToken r WHERE r.email = :email")
    Optional<DepartmentInviteToken> findByEmail(@Param("email") String email);


}
