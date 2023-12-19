package at.ac.tuwien.sepr.groupphase.backend.unittests;


import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class UserRepositoryTest implements TestData {

    @Autowired
    private UserRepositoryInterface userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.ADMIN)
        .build();
    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();
        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .withRole(Role.ADMIN)
            .build();
    }

    @Test
    public void savingNewUserAddsIntoDatabase() {
        userRepository.save(applicationUser);

        assertAll(
            () -> assertEquals(1, userRepository.findAll().size()),
            () -> assertNotNull(userRepository.findById(applicationUser.getId()))
        );

        Optional<ApplicationUser> savedUser = userRepository.findById(applicationUser.getId());
        assertTrue(savedUser.isPresent());
        ApplicationUser retrievedUser = savedUser.get();
        assertAll(
            () -> assertEquals(applicationUser.getId(), retrievedUser.getId()),
            () -> assertEquals(applicationUser.getFirstName(), retrievedUser.getFirstName()),
            () -> assertEquals(applicationUser.getLastName(), retrievedUser.getLastName()),
            () -> assertEquals(applicationUser.getEmail(), retrievedUser.getEmail())
        );
    }


    @Test
    public void savingUserWithSameEmailThrows() {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName("hello")
            .withLastName("world")
            .withEmail("hello@world1")
            .withPassword("helloworld")
            .withRole(Role.MANAGER)
            .build();
        userRepository.save(applicationUser);
        assertAll(
            () -> assertThrows(DataIntegrityViolationException.class, () -> {
                ApplicationUser applicationUser2 = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
                    .withId(2L)
                    .withFirstName("say")
                    .withLastName("my")
                    .withEmail("hello@world1")
                    .withPassword("namename")
                    .withRole(Role.MANAGER)
                    .build();
                userRepository.save(applicationUser2);
            })
        );
    }

}
