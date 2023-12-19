package at.ac.tuwien.sepr.groupphase.backend.basetest;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TestData {

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";

    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_MANAGER");
            add("ROLE_USER");
        }
    };
    List<String> MANAGER_ROLES = new ArrayList<>() {
        {
            add("ROLE_MANAGER");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

    String TEST_USER_FIRSTNAME = "hello";
    String TEST_USER_LASTNAME = "world";
    String TEST_USER_EMAIL = "hello@world";
    String TEST_USER_PASSWORD = "helloworld";


    String TEST_ADMIN_FIRSTNAME = "only";
    String TEST_ADMIN_LASTNAME = "admin";
    String TEST_ADMIN_EMAIL = "only@admin";
    String TEST_ADMIN_PASSWORD = "onlyadmin";


    String TEST_MANAGER_FIRSTNAME = "first";
    String TEST_MANAGER_LASTNAME = "manager";
    String TEST_MANAGER_EMAIL = "first@manager";
    String TEST_MANAGER_PASSWORD = "firstmanager";

    String TEST_DEPARTMENT_NAME = "first department";

    String TEST_PROJECT_NAME = "test_project";

    ApplicationUser TEST_DEPARTMENT_MANAGER = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.MANAGER)
        .build();

}
