package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class UserMapperTest implements TestData {

    private Department department= new Department();
    private ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(1L)
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.ADMIN)
        .withDepartment(new Department())
        .withAnswers(new LinkedList<>())
        .withProjects(new LinkedList<>())
        .build();

    @Autowired
    private UserMapper userMapper;

    private SimpleUserDto detailUserDto;
    @Autowired
    private DatabaseCleaner databaseCleaner;


    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();
        department.setName(TEST_DEPARTMENT_NAME);
        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .withRole(Role.ADMIN)
            .withDepartment(department)
            .build();
    }


    @Test
    public void givenNothing_whenMapDetailUserDtoToEntity_thenEntityHasAllProperties() {
        SimpleUserDto detailUserDto = userMapper.applicationUserToDetailedUserDto(applicationUser);
        assertAll(
            () -> assertEquals(TEST_USER_EMAIL, detailUserDto.email()),
            () -> assertEquals(TEST_USER_FIRSTNAME, detailUserDto.firstName()),
            () -> assertEquals(TEST_USER_LASTNAME, detailUserDto.lastName()),
            () -> assertEquals(TEST_USER_PASSWORD, detailUserDto.password()),
            () -> assertEquals(Role.ADMIN.name(), detailUserDto.role().toUpperCase()),
            () -> assertEquals(TEST_DEPARTMENT_NAME, detailUserDto.departmentName())
        );
    }

    @Test
    public void givenNothing_whenMapListDetailUserDtoToEntity_thenEntitiesHaveAllProperties() {
        List<ApplicationUser> applicationUsers = new LinkedList<>();
        applicationUsers.add(applicationUser);

        List<DetailUserDto> simpleUserDtos = userMapper.applicationUserToDetailUserDto(applicationUsers);
        assertAll(
            () -> assertEquals(TEST_USER_EMAIL, simpleUserDtos.get(0).getEmail()),
            () -> assertEquals(TEST_USER_FIRSTNAME,  simpleUserDtos.get(0).getFirstName()),
            () -> assertEquals(TEST_USER_LASTNAME,  simpleUserDtos.get(0).getLastName()),
            () -> assertEquals(TEST_USER_PASSWORD,  simpleUserDtos.get(0).getPassword()),
            () -> assertEquals(Role.ADMIN.name(),  simpleUserDtos.get(0).getRole().name()),
            () -> assertEquals(TEST_DEPARTMENT_NAME,  simpleUserDtos.get(0).getDepartment().name())
        );
    }


    @Test
    public void givenNothing_whenMapRegisterUserDtoToEntity_thenEntityHasAllProperties() {
        UserRegisterDto userRegisterDto = userMapper.applicationUserToRegisterUserDto(applicationUser);
        assertAll(
            () -> assertEquals(TEST_USER_FIRSTNAME, userRegisterDto.getFirstName()),
            () -> assertEquals(TEST_USER_LASTNAME, userRegisterDto.getLastName()),
            () -> assertEquals(TEST_USER_EMAIL, userRegisterDto.getUsername()),
            () -> assertEquals(TEST_USER_PASSWORD, userRegisterDto.getPassword())
        );
    }


    @Test
    public void givenNothing_whenEntityToMapDetailUserDto_thenDtoHasAllProperties() {
        detailUserDto = new SimpleUserDto(TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_EMAIL, TEST_USER_PASSWORD, Role.ADMIN.name(), TEST_DEPARTMENT_NAME);
        ApplicationUser applicationUser = userMapper.simpleUserDtoToApplicationUser(detailUserDto);
        assertAll(
            () -> assertEquals(TEST_USER_FIRSTNAME, applicationUser.getFirstName()),
            () -> assertEquals(TEST_USER_LASTNAME, applicationUser.getLastName()),
            () -> assertEquals(TEST_USER_EMAIL, applicationUser.getEmail()),
            () -> assertEquals(TEST_USER_PASSWORD, applicationUser.getPassword()),
            () -> assertEquals(Role.ADMIN, applicationUser.getRole())
        );
    }

}
