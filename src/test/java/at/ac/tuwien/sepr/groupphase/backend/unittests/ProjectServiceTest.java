package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
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
import at.ac.tuwien.sepr.groupphase.backend.repository.ProjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.service.DepartmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.ProjectService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ProjectServiceTest implements TestData {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepositoryInterface userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(1L)
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.ADMIN)
        .build();

    private Project project = Project.ProjectBuilder.aDefaultProject().withId(ID)
        .withName(TEST_PROJECT_NAME)
        .withMembers(new ArrayList<>())
        .build();

    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();

        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .withRole(Role.ADMIN)
            .build();
    }

    @Test
    public void creatingProjectReturnsCorrectProject() {
        var savedUser = userRepository.save(applicationUser);
        List list = new ArrayList<>();
        departmentService.createDepartment("hi", savedUser.getEmail(), list);
        ProjectCreateDto projectCreateDto = ProjectCreateDto.ProjectCreateDtoBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withManagerEmail(savedUser.getEmail())
            .build();
        Project newProject = projectService.createProject(projectCreateDto);
        assertAll("Project create returns",
            () -> assertNotNull(newProject),
            () -> assertEquals(TEST_PROJECT_NAME, newProject.getName()),
            () -> assertEquals(new ArrayList<>(), newProject.getMembers()));
    }

    @Test
    @Transactional
    public void creatingProjectStoresTheProjectCorrectly() {
        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail("TestEmail")
            .withPassword(TEST_USER_PASSWORD)
            .withRole(Role.ADMIN)
            .build();
        var savedUser = userRepository.save(applicationUser);
        List<String> list = new ArrayList<>();
        departmentService.createDepartment("hi", savedUser.getEmail(), list);
        ProjectCreateDto projectCreateDto = ProjectCreateDto.ProjectCreateDtoBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withManagerEmail(savedUser.getEmail())
            .build();
        var createdProject = projectService.createProject(projectCreateDto);
        Project newProject = projectRepository.findById(createdProject.getId()).orElse(null);
        if (newProject != null) {
            Hibernate.initialize(newProject.getMembers());
        }
        assertAll("Project create stores",
            () -> assertNotNull(newProject),
            () -> assertEquals(TEST_PROJECT_NAME, newProject.getName()),
            () -> assertEquals(new ArrayList<>(), newProject.getMembers()));
    }

    @Test
    public void creatingProjectWithInvalidDepartmentThrows404Exception() {
        ProjectCreateDto projectDto = new ProjectCreateDto();
        projectDto.setName("test");
        projectDto.setManagerEmail("nonExistantManager@email.com");
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> projectService.createProject(projectDto)),
            () -> {
                NotFoundException exception = assertThrows(NotFoundException.class, () -> projectService.createProject(projectDto));
                assertEquals("Department could not be found", exception.getMessage());
            }
        );
    }

    @Test
    @Transactional
    public void getByIdReturnsTheCorrectProject() {
        Project projectToCreate = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withMembers(new ArrayList<>())
            .build();
        Project createdProject = projectRepository.save(projectToCreate);
        Project newProject = projectService.getById(createdProject.getId());
        if (newProject != null) {
            Hibernate.initialize(project.getMembers());
        }
        assertAll("GetById() returns",
            () -> assertNotNull(newProject),
            () -> assertEquals(TEST_PROJECT_NAME, newProject.getName()),
            () -> assertEquals(new ArrayList<>(), newProject.getMembers()));
    }

    @Test
    public void getByIdWithInvalidIDThrows404Exception() {
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> projectService.getById(-1L)),
            () -> {
                NotFoundException exception = assertThrows(NotFoundException.class, () -> projectService.getById(-1L));
                assertEquals("Project with given id cannot be found", exception.getMessage());
            }
        );
    }

    @Test
    void gettingAllFromDepartmentReturnsExactlyTheProjectsOfADepartment() {
        //departments
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName("manager")
            .withLastName("world")
            .withEmail("manager1@email")
            .withPassword("helloworld")
            .withRole(Role.MANAGER)
            .build();
        var savedUser = userRepository.save(applicationUser);
        Department department = new Department();
        department.setName("test");
        department.setManager(savedUser);
        department.setMembers(new ArrayList<>());
        department.setProjects(new ArrayList<>());
        department = departmentRepository.save(department);

        ApplicationUser applicationUser2 = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName("manager")
            .withLastName("world")
            .withEmail("manager2@email")
            .withPassword("helloworld")
            .withRole(Role.MANAGER)
            .build();
        var savedUser2 = userRepository.save(applicationUser2);
        Department department2 = new Department();
        department2.setName("test2");
        department2.setManager(savedUser2);
        department2.setMembers(new ArrayList<>());
        department2.setProjects(new ArrayList<>());
        department2 = departmentRepository.save(department2);

        //projects
        Project projectToCreate = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withMembers(new ArrayList<>())
            .withDepartment(department)
            .build();
        Project createdProject = projectRepository.save(projectToCreate);

        Project projectToCreate2 = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withMembers(new ArrayList<>())
            .withDepartment(department2)
            .build();
        Project createdProject2 = projectRepository.save(projectToCreate2);

        //test
        var result = projectService.getAllOfDepartment("manager1@email");
        assertAll("updateProject() returns",
            () -> assertNotNull(result),
            () -> assertEquals(1, result.size()),
            () -> assertEquals(createdProject.getId(), result.get(0).getId()));

    }

    @Test
    public void addingMemberWithUpdateProjectReturnsUpdatedProject() {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName("manager")
            .withLastName("world")
            .withEmail("manager1@email")
            .withPassword("helloworld")
            .withRole(Role.MANAGER)
            .build();
        var savedUser = userRepository.save(applicationUser);
        Department department = new Department();
        department.setName("test");
        department.setManager(savedUser);
        department.setMembers(new ArrayList<>());
        department.setProjects(new ArrayList<>());
        department = departmentRepository.save(department);

        Project projectToCreate = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withMembers(new ArrayList<>())
            .withDepartment(department)
            .build();
        Project createdProject = projectRepository.save(projectToCreate);

        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName("member")
            .withLastName("world")
            .withEmail("member@email")
            .withPassword("helloworld")
            .withRole(Role.MANAGER)
            .build();
        applicationUser.setDepartment(department);
        savedUser = userRepository.save(applicationUser);

        var detailUser = userMapper.applicationUserToDetailedUserDto(savedUser);

        var newList = new ArrayList<SimpleUserDto>();
        newList.add(detailUser);

        DetailProjectDto updatedProject = DetailProjectDto.DetailProjectDtoBuilder.aDefaultProject()
            .withId(createdProject.getId())
            .withName(TEST_PROJECT_NAME)
            .withMembers(newList)
            .build();

        var returnedProject = projectService.updateProject(updatedProject);

        assertAll("updateProject() returns",
            () -> assertNotNull(returnedProject),
            () -> assertEquals(TEST_PROJECT_NAME, returnedProject.getName()),
            () -> assertEquals("member@email", returnedProject.getMembers().get(0).getEmail()));
    }

    @Test
    public void updateProjectWithInvalidIDThrows404Exception() {
        DetailProjectDto projectDto = new DetailProjectDto();
        projectDto.setId(-1L);
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> projectService.updateProject(projectDto)),
            () -> {
                NotFoundException exception = assertThrows(NotFoundException.class, () -> projectService.getById(-1L));
                assertEquals("Project with given id cannot be found", exception.getMessage());
            }
        );
    }

    @Test
    public void removingMemberWithUpdateProjectReturnsUpdatedProject() {

        var savedUser = userRepository.save(applicationUser);

        var list = new ArrayList<ApplicationUser>();
        list.add(savedUser);

        Project projectToCreate = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withMembers(list)
            .build();
        Project createdProject = projectRepository.save(projectToCreate);


        var newList = new ArrayList<SimpleUserDto>();

        DetailProjectDto updatedProject = DetailProjectDto.DetailProjectDtoBuilder.aDefaultProject()
            .withId(createdProject.getId())
            .withName(TEST_PROJECT_NAME)
            .withMembers(newList)
            .build();
        var returnedProject = projectService.updateProject(updatedProject);

        assertAll("updateProject() returns",
            () -> assertNotNull(returnedProject),
            () -> assertEquals(TEST_PROJECT_NAME, returnedProject.getName()),
            () -> assertEquals(0, returnedProject.getMembers().size()));
    }

    @Test
    public void deletingProjectRemovesItFromPersistentStorage() {
        Project projectToCreate = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withMembers(new ArrayList<>())
            .build();
        Project createdProject = projectRepository.save(projectToCreate);

        projectService.deleteProject(createdProject.getId());

        var allProjects = projectRepository.findAll();

        assertAll("updateProject() returns",
            () -> assertNotNull(allProjects),
            () -> assertEquals(0, allProjects.size()));
    }
}
