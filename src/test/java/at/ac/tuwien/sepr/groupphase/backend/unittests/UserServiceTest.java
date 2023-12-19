package at.ac.tuwien.sepr.groupphase.backend.unittests;


import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdatePasswordRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSuggestionsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.DepartmentInviteToken;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetPasswordToken;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.InviteTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ProjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ResetPasswordRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class UserServiceTest implements TestData {

    @Autowired
    private UserRepositoryInterface userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private ResetPasswordRepository resetPasswordRepository;
    @Autowired
    private InviteTokenRepository inviteTokenRepository;
    @Autowired
    private CustomUserDetailService service;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ProjectRepository projectRepository;


    private ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(1L)
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.USER)
        .withDepartment(null)
        .withAnswers(null)
        .build();

    private ApplicationUser applicationManager = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(1L)
        .withFirstName(TEST_MANAGER_FIRSTNAME)
        .withLastName(TEST_MANAGER_LASTNAME)
        .withEmail(TEST_MANAGER_EMAIL)
        .withPassword(TEST_MANAGER_PASSWORD)
        .withRole(Role.MANAGER)
        .build();

    private ApplicationUser managerUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withId(1L)
        .withFirstName(TEST_MANAGER_FIRSTNAME)
        .withLastName(TEST_MANAGER_LASTNAME)
        .withEmail(TEST_MANAGER_EMAIL)
        .withPassword(TEST_MANAGER_PASSWORD)
        .withRole(Role.MANAGER)
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
            .withRole(Role.USER)
            .withDepartment(null)
            .withAnswers(null)
            .build();
    }


    @Test
    public void loadValidUserByUserNameGetsTheUser() {
        userRepository.save(applicationUser);
        UserDetails userdetails = service.loadUserByUsername(TEST_USER_EMAIL);
        assertAll(
            () -> assertNotNull(userdetails),
            () -> assertEquals(TEST_USER_EMAIL, userdetails.getUsername()),
            () -> assertEquals(TEST_USER_PASSWORD, userdetails.getPassword()),
            () -> assertTrue(authoritiesContainRole(userdetails.getAuthorities(), "ROLE_USER"))
        );
    }

    @Test
    public void loadValidUserByUserNameGetsTheManager() {
        userRepository.save(applicationManager);
        UserDetails userdetails = service.loadUserByUsername(TEST_MANAGER_EMAIL);
        assertAll(
            () -> assertNotNull(userdetails),
            () -> assertEquals(TEST_MANAGER_EMAIL, userdetails.getUsername()),
            () -> assertEquals(TEST_MANAGER_PASSWORD, userdetails.getPassword()),
            () -> assertTrue(authoritiesContainRole(userdetails.getAuthorities(), "ROLE_MANAGER"))
        );
    }

    private boolean authoritiesContainRole(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.stream().anyMatch(authority -> authority.getAuthority().equals(role));
    }

    @Test
    public void gettingValidUserGetsTheUser() {
        userRepository.save(applicationUser);
        ApplicationUser savedUser = service.findUserByEmail(TEST_USER_EMAIL);
        assertAll(
            () -> assertNotNull(savedUser),
            () -> assertEquals(TEST_USER_FIRSTNAME, savedUser.getFirstName()),
            () -> assertEquals(TEST_USER_LASTNAME, savedUser.getLastName()),
            () -> assertEquals(TEST_USER_EMAIL, savedUser.getEmail()),
            () -> assertEquals(TEST_USER_PASSWORD, savedUser.getPassword())
        );
    }

    @Test
    public void gettingInvalidEmailThrows404() {
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> service.findUserByEmail("non@existing")),
            () -> {
                NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findUserByEmail("non@existing"));
                assertEquals("Could not find the user with the email address non@existing", exception.getMessage());
            }
        );
    }

    @Test
    public void loginUserLogesUserIn() {
        applicationUser.setEmail("manager1@email.com");
        applicationUser.setPassword("$2a$10$/9QuBk6UMfHeeGpIvGsNW.yO/4ky834ISNqwUiPnrLlsfuKd.cada");
        userRepository.save(applicationUser);
        UserLoginDto userLoginDto = new UserLoginDto();

        userLoginDto.setEmail("manager1@email.com");
        userLoginDto.setPassword("password");
        String token = service.login(userLoginDto);
        assertAll(
            () -> assertNotNull(token),
            () -> assertTrue(token.startsWith("Bearer"))
        );
    }

    @Test
    public void loginUserWithInvalidCredentialsThrows() {
        userRepository.save(applicationUser);
        UserLoginDto userLoginDto = new UserLoginDto();

        userLoginDto.setEmail(TEST_USER_EMAIL);
        userLoginDto.setPassword("invalidPassword");

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            service.login(userLoginDto);
        });
        assertEquals("Username or password is incorrect", exception.getMessage());
    }

    @Test
    public void registerValidUserRegistersUser() {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setFirstName(TEST_USER_FIRSTNAME);
        userRegisterDto.setLastName(TEST_USER_LASTNAME);
        userRegisterDto.setUsername(TEST_USER_EMAIL);
        userRegisterDto.setPassword(TEST_USER_PASSWORD);
        userRegisterDto.setMode("USER");

        SimpleUserDto result = service.register(userRegisterDto);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(TEST_USER_FIRSTNAME, result.firstName()),
            () -> assertEquals(TEST_USER_LASTNAME, result.lastName()),
            () -> assertEquals(TEST_USER_EMAIL, result.email()),
            () -> assertEquals(Role.USER.toString(), result.role())
        );
    }

    @Test
    public void registerAlreadyExistingUserThrows() {
        userRepository.save(applicationUser);
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setFirstName(TEST_USER_FIRSTNAME);
        userRegisterDto.setLastName(TEST_USER_LASTNAME);
        userRegisterDto.setUsername(TEST_USER_EMAIL);
        userRegisterDto.setPassword(TEST_USER_PASSWORD);
        userRegisterDto.setMode("USER");

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            service.register(userRegisterDto);
        });
        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    public void deletingNonExistingUserThrows404() {
        assertThrows(NotFoundException.class, () -> {
            service.delete("non@existing");
        });
    }

    @Test
    public void deletingExistingUserTwiceThrows404() {

        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName("hello")
            .withLastName("world")
            .withEmail("hello@world")
            .withPassword("helloworld")
            .withRole(Role.USER)
            .build();
        userRepository.save(applicationUser);
        assertEquals(1, userRepository.findAll().size());
        service.delete(TEST_USER_EMAIL);
        assertAll(
            () -> assertEquals(0, userRepository.findAll().size()),
            () -> assertThrows(NotFoundException.class, () -> service.delete(TEST_USER_EMAIL))
        );

    }

    @Test
    public void upgradeUserToManagerUpgradesUser() {
        userRepository.save(applicationUser);
        SimpleUserDto result = service.upgradeUserToManager(TEST_USER_EMAIL);
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(TEST_USER_FIRSTNAME, result.firstName()),
            () -> assertEquals(TEST_USER_LASTNAME, result.lastName()),
            () -> assertEquals(TEST_USER_EMAIL, result.email()),
            () -> assertEquals(Role.MANAGER.toString(), result.role())
        );
    }

    @Test
    public void upgradeManagerToManagerThrows() {
        userRepository.save(applicationManager);
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            service.upgradeUserToManager(applicationManager.getEmail());
        });
        assertEquals("User has already role MANAGER", exception.getMessage());
    }

    @Test
    public void upgradeNonExistingUserThrows() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.upgradeUserToManager(applicationUser.getEmail());
        });
        assertEquals("Could not find user " + applicationUser.getEmail() + " to upgrade", exception.getMessage());
    }

    @Test
    public void updateValidEmailUpdatesInDatabase() {

        userRepository.save(applicationUser);

        SimpleUserDto toUpdate = new SimpleUserDto("hi", TEST_USER_LASTNAME, TEST_USER_EMAIL, TEST_USER_PASSWORD, Role.USER.name(), null);
        service.update(toUpdate);
        ApplicationUser updatedUser = service.findUserByEmail(TEST_USER_EMAIL);

        assertAll(
            () -> assertNotNull(updatedUser),
            () -> assertNotNull(updatedUser.getId()),
            () -> assertEquals("hi", updatedUser.getFirstName()),
            () -> assertEquals(TEST_USER_LASTNAME, updatedUser.getLastName()),
            () -> assertEquals(TEST_USER_EMAIL, updatedUser.getEmail()),
            () -> assertEquals(TEST_USER_PASSWORD, updatedUser.getPassword())
        );
    }

    @Test
    public void updateInvalidEmailThrows404() {
        SimpleUserDto toUpdate = new SimpleUserDto("Anton", "Bauer", "Anton.Bauer@gmx.at", "antonbauer", Role.USER.name(), null);
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> service.update(toUpdate)),
            () -> {
                NotFoundException exception = assertThrows(NotFoundException.class, () -> service.update(toUpdate));
                assertEquals("Could not find Anton.Bauer@gmx.at to update", exception.getMessage());
            }
        );
    }

    @Test
    public void sendResetPasswordEmailDoesNotThrow() {
        userRepository.save(applicationUser);
        assertAll(
            () -> assertDoesNotThrow(() -> service.resetPassword(TEST_USER_EMAIL))
        );
    }

    @Test
    public void findUserByNameWithEmptyUserSearchDtoFindsAllUsers() {
        userRepository.save(applicationUser);
        userRepository.save(applicationManager);
        List<ApplicationUser> result = service.findUserByName(new UserSearchDto());
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(2, result.size())

        );
    }

    @Test
    public void sendResetPasswordEmailWithNoEmailThrowsError() {
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> service.resetPassword("blank"))
        );
    }

    @Test
    public void findUserByNameFindUsers() {
        userRepository.save(applicationUser);
        userRepository.save(applicationManager);
        ApplicationUser applicationUser1 = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail(TEST_USER_EMAIL + ".at")
            .withPassword(TEST_USER_PASSWORD)
            .withRole(Role.USER)
            .build();
        userRepository.save(applicationUser1);
        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.setFirstInput(TEST_USER_FIRSTNAME);

        List<ApplicationUser> result = service.findUserByName(userSearchDto);
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(2, result.size())

        );
    }

    @Test
    public void sendResetPasswordThreeTimesDoesNotThrow() {
        userRepository.save(applicationUser);
        assertAll(
            () -> assertDoesNotThrow(() -> service.resetPassword(TEST_USER_EMAIL)),
            () -> assertDoesNotThrow(() -> service.resetPassword(TEST_USER_EMAIL)),
            () -> assertDoesNotThrow(() -> service.resetPassword(TEST_USER_EMAIL))
        );
    }

    @Test
    public void findUserByNonExistingNameReturnsEmptyList() {
        userRepository.save(applicationUser);
        userRepository.save(applicationManager);
        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.setFirstInput("nonExisting");

        List<ApplicationUser> result = service.findUserByName(userSearchDto);
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(0, result.size())
        );
    }

    @Test
    public void updatePasswordWithValidTokenReturnsTrue() {
        userRepository.save(applicationUser);
        String token = service.resetPassword(TEST_USER_EMAIL);

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setNewPassword("newPassword");
        request.setResetToken(token);

        assertAll(
            () -> assertEquals(true, service.updatePassword(request))
        );
    }

    @Test
    public void findAllUsersReturnsAllUsers() {
        userRepository.save(applicationUser);
        userRepository.save(applicationManager);
        List<SimpleUserDto> result = service.findAllUsers();
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(2, result.size())

        );
    }

    @Test
    public void updatePasswordWithInvalidTokenThrows() {
        userRepository.save(applicationUser);
        String tokenToReset = service.resetPassword(TEST_USER_EMAIL);

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setNewPassword("newPassword");
        request.setResetToken("blank");

        assertAll(
            () -> assertThrows(NotFoundException.class, () -> service.updatePassword(request))
        );
    }

    @Test
    public void updatePasswordWithExpiredTokenThrows() {
        userRepository.save(applicationUser);
        String tokenToReset = service.resetPassword(TEST_USER_EMAIL);

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setNewPassword("newPassword");
        request.setResetToken(tokenToReset);

        ResetPasswordToken resetPasswordToken = resetPasswordRepository.findByEmail(TEST_USER_EMAIL).orElse(null);
        if(resetPasswordToken != null) {
            resetPasswordToken.setLocalDateTime(resetPasswordToken.getLocalDateTime().minusHours(2));
            resetPasswordRepository.save(resetPasswordToken);
        }

        assertAll(
            () -> assertThrows(NotFoundException.class, () -> service.updatePassword(request))
        );
    }

    @Test
    public void inviteUserToDepartmentWithExistingTokenSuccessful() {

        userRepository.save(applicationUser);
        userRepository.save(managerUser);

        Department department = new Department(1L, "department 1", managerUser, null);
        departmentRepository.save(department);


        SimpleUserDto manager = new SimpleUserDto(managerUser.getFirstName(), managerUser.getLastName(), managerUser.getEmail(), managerUser.getPassword(), managerUser.getRole().toString(), "");

        service.inviteToDepartment(manager, applicationUser.getEmail());

        String token = service.inviteToDepartment(manager, applicationUser.getEmail());

        assertNotNull(token);
    }

    @Test
    public void inviteUserToDepartmentWithoutValidUserThrows() {

        userRepository.save(applicationUser);
        userRepository.save(managerUser);

        SimpleUserDto manager = new SimpleUserDto(managerUser.getFirstName(), managerUser.getLastName(), managerUser.getEmail() + ".nl.xz", managerUser.getPassword(), managerUser.getRole().toString(), "");


        assertThrows(NotFoundException.class, () -> service.inviteToDepartment(manager, applicationUser.getEmail()));
    }

    @Test
    public void inviteUserToDepartmentWithoutValidMemberThrows() {

        userRepository.save(applicationUser);
        userRepository.save(managerUser);

        SimpleUserDto manager = new SimpleUserDto(managerUser.getFirstName(), managerUser.getLastName(), managerUser.getEmail(), managerUser.getPassword(), managerUser.getRole().toString(), "");

        assertThrows(NotFoundException.class, () -> service.inviteToDepartment(manager, applicationUser.getEmail() + ".nl.xz"));
    }


    @Test
    public void addUserToDepartmentWithValidData() {

        userRepository.save(applicationUser);
        userRepository.save(managerUser);

        Department department = new Department(1L, "department 1", managerUser, null);
        departmentRepository.save(department);

        SimpleUserDto manager = new SimpleUserDto(managerUser.getFirstName(), managerUser.getLastName(), managerUser.getEmail(), managerUser.getPassword(), managerUser.getRole().toString(), "");

        String token = service.inviteToDepartment(manager, applicationUser.getEmail());

        System.out.println(managerUser.getEmail() + ", " + applicationUser.getEmail() + ", " + token);
        service.addToDepartment(managerUser.getEmail(), applicationUser.getEmail(), token);

        assertAll(
            () -> assertNotNull(userRepository.findByEmail(applicationUser.getEmail()).get().getDepartment()),
            () -> assertEquals(userRepository.findByEmail(applicationUser.getEmail()).get().getDepartment().getName(), "department 1")
        );
    }

    @Test
    public void addUserToDepartmentWithExpiredTokenThrows() {

        userRepository.save(applicationUser);
        userRepository.save(managerUser);

        Department department = new Department(1L, "department 1", managerUser, null);
        departmentRepository.save(department);

        SimpleUserDto manager = new SimpleUserDto(managerUser.getFirstName(), managerUser.getLastName(), managerUser.getEmail(), managerUser.getPassword(), managerUser.getRole().toString(), "");

        String token = service.inviteToDepartment(manager, applicationUser.getEmail());

        DepartmentInviteToken departmentInviteToken = inviteTokenRepository.findByManagerAndEmail(managerUser.getEmail(), applicationUser.getEmail()).orElse(null);

        if (departmentInviteToken != null) {
            departmentInviteToken.setLocalDateTime(LocalDateTime.now().minusDays(22));
            inviteTokenRepository.save(departmentInviteToken);
        }

        assertThrows(NotFoundException.class, () -> service.addToDepartment(managerUser.getEmail(), applicationUser.getEmail(), token));
    }

    @Test
    public void addUserToDepartmentWithoutDepartmentThrows() {

        userRepository.save(applicationUser);
        userRepository.save(managerUser);

        Department department = new Department(1L, "department 1", managerUser, null);
        departmentRepository.save(department);

        SimpleUserDto manager = new SimpleUserDto(managerUser.getFirstName(), managerUser.getLastName(), managerUser.getEmail(), managerUser.getPassword(), managerUser.getRole().toString(), "");

        String token = service.inviteToDepartment(manager, applicationUser.getEmail());

        Department department1 = departmentRepository.findByManagerEmail(managerUser.getEmail()).orElse(null);
        if (department1 != null) {
            departmentRepository.delete(department1);
        }

        assertThrows(NotFoundException.class, () -> service.addToDepartment(managerUser.getEmail(), applicationUser.getEmail(), token));
    }

    @Test
    public void addUserToDepartmentWithInvalidData() {

        userRepository.save(applicationUser);
        userRepository.save(managerUser);

        Department department = new Department(1L, "department 1", managerUser, null);
        departmentRepository.save(department);

        SimpleUserDto manager = new SimpleUserDto(managerUser.getFirstName(), managerUser.getLastName(), managerUser.getEmail(), managerUser.getPassword(), managerUser.getRole().toString(), "");

        service.inviteToDepartment(manager, applicationUser.getEmail());
        String token = "blank";

        service.addToDepartment(managerUser.getEmail(), applicationUser.getEmail(), token);

        assertNull(userRepository.findByEmail(applicationUser.getEmail()).get().getDepartment());
    }

    @Test
    @Transactional
    public void findUserSuggestionsByNameReturnsValidUser() {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName("manager")
            .withLastName("world")
            .withEmail("manager@email")
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

        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName("hello")
            .withLastName("world")
            .withEmail("hello@world")
            .withPassword("helloworld")
            .withRole(Role.USER)
            .build();
        applicationUser.setDepartment(department);
        savedUser = userRepository.save(applicationUser);

        department.getMembers().add(savedUser);
        department = departmentRepository.save(department);

        List<ApplicationUser> list = new ArrayList<>();
        list.add(savedUser);

        Project projectToCreate = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withMembers(new ArrayList<>())
            .withDepartment(department)
            .build();
        Project createdProject = projectRepository.save(projectToCreate);

        UserSuggestionsDto searchDto = new UserSuggestionsDto();
        searchDto.setProjectId(createdProject.getId().intValue());
        searchDto.setManagerEmail("manager@email");

        var result = service.findSuggestionsByName(searchDto);

        assertAll("Get suggestions for users",
            () -> assertNotNull(result),
            () -> assertEquals(1, result.size()),
            () -> assertEquals("hello@world", result.get(0).getEmail())
        );
    }

    @Test
    @Transactional
    public void findUserSuggestionsByNameDoesntReturnUserAlreadyInProject() {
        ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName("manager")
            .withLastName("world")
            .withEmail("managerfailed@email")
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

        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withId(1L)
            .withFirstName("hello")
            .withLastName("world")
            .withEmail("failed@world")
            .withPassword("helloworld")
            .withRole(Role.USER)
            .build();
        applicationUser.setDepartment(department);
        savedUser = userRepository.save(applicationUser);

        department.getMembers().add(savedUser);
        department = departmentRepository.save(department);

        List<ApplicationUser> list = new ArrayList<>();
        list.add(savedUser);

        Project projectToCreate = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withMembers(list)
            .withDepartment(department)
            .build();
        Project createdProject = projectRepository.save(projectToCreate);

        UserSuggestionsDto searchDto = new UserSuggestionsDto();
        searchDto.setProjectId(createdProject.getId().intValue());
        searchDto.setManagerEmail("manager@email");

        var result = service.findSuggestionsByName(searchDto);

        assertAll("Get suggestions for users",
            () -> assertNotNull(result),
            () -> assertEquals(0, result.size())
        );
    }
}
