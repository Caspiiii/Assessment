package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPasswordToken, String> {
    @Query("SELECT r.email FROM ResetPasswordToken r WHERE r.token = :token")
    String findEmailByToken(@Param("token") String token);

    @Query("SELECT r.localDateTime FROM ResetPasswordToken r WHERE r.token = :token")
    LocalDateTime findLocalDateTimeByToken(@Param("token") String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM ResetPasswordToken r WHERE r.token = :token")
    void deleteByToken(@Param("token") String token);

    @Query("SELECT r FROM ResetPasswordToken r WHERE r.email = :email")
    Optional<ResetPasswordToken> findByEmail(@Param("email") String email);
}
