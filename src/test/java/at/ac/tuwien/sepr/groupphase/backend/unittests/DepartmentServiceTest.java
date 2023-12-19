package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailProjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ProjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.service.DepartmentService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.basetest.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class DepartmentServiceTest {


    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepositoryInterface userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private ApplicationUser admin = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(1L)
        .withFirstName(TEST_ADMIN_FIRSTNAME)
        .withLastName(TEST_ADMIN_LASTNAME)
        .withEmail(TEST_ADMIN_EMAIL)
        .withPassword(TEST_ADMIN_PASSWORD)
        .withRole(Role.ADMIN)
        .build();

    private ApplicationUser manager = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(2L)
        .withFirstName(TEST_MANAGER_FIRSTNAME)
        .withLastName(TEST_MANAGER_LASTNAME)
        .withEmail(TEST_MANAGER_EMAIL)
        .withPassword(TEST_MANAGER_PASSWORD)
        .withRole(Role.MANAGER)
        .build();

    private Department department = new Department(TEST_DEPARTMENT_NAME, manager, new ArrayList<>(), new ArrayList<>());

    private Department departmentWithId = new Department(5L, TEST_DEPARTMENT_NAME, manager, new ArrayList<>());

    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();

        admin = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .withRole(Role.ADMIN)
            .build();

        manager = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(2L)
            .withFirstName(TEST_MANAGER_FIRSTNAME)
            .withLastName(TEST_MANAGER_LASTNAME)
            .withEmail(TEST_MANAGER_EMAIL)
            .withPassword(TEST_MANAGER_PASSWORD)
            .withRole(Role.MANAGER)
            .build();
    }

    @Test
    public void creatingDepartmentReturnsCorrectDepartment() {
        var savedManager = userRepository.save(manager);
        DepartmentDto departmentDto = departmentService.createDepartment(TEST_DEPARTMENT_NAME, savedManager.getEmail(), new ArrayList<>());

        assertAll("Department create returns",
            () -> assertNotNull(departmentDto),
            () -> assertEquals(TEST_DEPARTMENT_NAME, departmentDto.name()),
            () -> assertEquals(new ArrayList<>(), departmentDto.members()));
    }

    @Test
    @Transactional
    public void creatingDepartmentStoresTheDepartmentCorrectly() {
        var savedManager = userRepository.save(manager);
        DepartmentDto departmentDto = departmentService.createDepartment(TEST_DEPARTMENT_NAME, savedManager.getEmail(), new ArrayList<>());
        Department newDepartment = departmentRepository.findById(departmentDto.id()).orElse(null);
        if (newDepartment != null) {
            Hibernate.initialize(newDepartment.getMembers());
        }
        assertAll("Correct department creation",
            () -> assertNotNull(newDepartment),
            () -> assertEquals(TEST_DEPARTMENT_NAME, newDepartment.getName()),
            () -> assertEquals(new ArrayList<>(), newDepartment.getMembers()));
    }

    @Test
    public void creatingDepartmentWithNonExistingManagerThrowsException() {
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> departmentService.createDepartment(TEST_DEPARTMENT_NAME, "invalidmanager@email.com", new ArrayList<>())),
            () -> {
                NotFoundException exception = assertThrows(NotFoundException.class, () -> departmentService.createDepartment(TEST_DEPARTMENT_NAME, "invalidmanager@email.com", new ArrayList<>()));
                assertEquals("User with email invalidmanager@email.com not found", exception.getMessage());
            }
        );
    }

    @Test
    @Transactional
    public void getDepartmentByIdReturnsCorrectDepartment() {
        var savedManager = userRepository.save(manager);
        DepartmentDto departmentDto = departmentService.createDepartment(TEST_DEPARTMENT_NAME, savedManager.getEmail(), new ArrayList<>());
        DepartmentDto newDepartment = departmentService.getDepartmentById(departmentDto.id());
        assertAll("GetById() returns",
            () -> assertNotNull(newDepartment),
            () -> assertEquals(TEST_DEPARTMENT_NAME, newDepartment.name()),
            () -> assertEquals(new ArrayList<>(), newDepartment.members()));
    }


    @Test
    public void getDepartmentByIdWithInvalidIDThrows404Exception() {
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> departmentService.getDepartmentById(-1L)),
            () -> {
                NotFoundException exception = assertThrows(NotFoundException.class, () -> departmentService.getDepartmentById(-1L));
                assertEquals("Department with id -1 not found", exception.getMessage());
            }
        );
    }

    @Test
    void getAllDepartmentsReturnsCorrectDepartments() {
        var savedManager = userRepository.save(manager);
        DepartmentDto departmentDto = departmentService.createDepartment(TEST_DEPARTMENT_NAME, savedManager.getEmail(), new ArrayList<>());
        List<DepartmentDto> allDepartments = departmentService.getAllDepartments();
        assertAll("GetAll() returns",
            () -> assertNotNull(allDepartments),
            () -> assertEquals(1, allDepartments.size()),
            () -> assertEquals(TEST_DEPARTMENT_NAME, allDepartments.get(0).name()),
            () -> assertEquals(new ArrayList<>(), allDepartments.get(0).members()));
    }

    @Test
    @Transactional
    public void updateDepartmentReturnsCorrectDepartment() {
        var savedManager = userRepository.save(manager);
        DepartmentDto departmentDto = departmentService.createDepartment(TEST_DEPARTMENT_NAME, savedManager.getEmail(), new ArrayList<>());
        DepartmentDto updatedDepartment = departmentService.updateDepartment(departmentDto.id(), "newName", savedManager.getEmail(), new ArrayList<>());
        assertAll("Update() returns",
            () -> assertNotNull(updatedDepartment),
            () -> assertEquals("newName", updatedDepartment.name()),
            () -> assertEquals(new ArrayList<>(), updatedDepartment.members()));
    }

    @Test
    public void updateDepartmentWithInvalidIDThrows404Exception() {
        var savedManager = userRepository.save(manager);
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> departmentService.updateDepartment(-1L, "newName", manager.getEmail(), new ArrayList<>())),
            () -> {
                NotFoundException exception = assertThrows(NotFoundException.class, () -> departmentService.updateDepartment(-1L, "newName", manager.getEmail(), new ArrayList<>()));
                assertEquals("Department with id -1 not found", exception.getMessage());
            }
        );
    }

}
