package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.repository.DepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ProjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class UserEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepositoryInterface userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private DatabaseCleaner databaseCleaner;
    private String User_BASE_URI = BASE_URI + "/user";
    private Department department = new Department();

    private Project project = Project.ProjectBuilder.aDefaultProject().withId(ID)
        .withName(TEST_PROJECT_NAME)
        .withMembers(new ArrayList<>())
        .build();

    private ApplicationUser applicationAdmin = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.ADMIN)
        .build();
    private ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL + ".at")
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.USER)
        .build();
    private ApplicationUser applicationManager = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withFirstName(TEST_MANAGER_FIRSTNAME)
        .withLastName(TEST_MANAGER_LASTNAME)
        .withEmail(TEST_MANAGER_EMAIL)
        .withPassword(TEST_MANAGER_PASSWORD)
        .withRole(Role.MANAGER)
        .build();

    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();
        department.setName(TEST_DEPARTMENT_NAME);
        department.setProjects(new ArrayList<>());
        departmentRepository.save(department);
        project.setDepartment(department);
        projectRepository.save(project);
        Hibernate.initialize(department.getProjects());
        List<Project> projectList = new ArrayList<>();
        projectList.add(project);

        applicationAdmin = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .withRole(Role.ADMIN)
            .build();
        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail(TEST_USER_EMAIL + ".at")
            .withPassword(TEST_USER_PASSWORD)
            .withDepartment(department)
            .withProjects(projectList)
            .withRole(Role.USER)
            .build();
    }

    @Test
    public void findValidUserByMailFindsUserWithAllProperties() throws Exception {

        userRepository.save(applicationManager);
        MvcResult mvcResult = this.mockMvc.perform(get(User_BASE_URI + "/byEmail/{email}", applicationManager.getEmail())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        SimpleUserDto userDto = objectMapper.readValue(content, SimpleUserDto.class);
        assertAll(
            () -> assertEquals(applicationManager.getEmail(), userDto.email()),
            () -> assertEquals(applicationManager.getFirstName(), userDto.firstName()),
            () -> assertEquals(applicationManager.getLastName(), userDto.lastName()),
            () -> assertEquals(applicationManager.getRole().name(), userDto.role())
        );

    }

    @Test
    public void findNonExistingUserByMailReturns404() throws Exception {
        userRepository.save(applicationUser);
        applicationUser.setEmail("non@existing");

        MvcResult mvcResult = this.mockMvc.perform(get(User_BASE_URI + "/byEmail/{email}", applicationUser.getEmail())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Could not find the user with the email address non@existing", response);
    }

    @Test
    public void updateValidUserByMailUpdatesUserWithAllProperties() throws Exception {
        userRepository.save(applicationUser);
        applicationUser.setFirstName("Anna");
        String updatedUserJson = new ObjectMapper().writeValueAsString(applicationUser);

        MvcResult mvcResult = this.mockMvc.perform(put(User_BASE_URI + "/{email}", applicationUser.getEmail())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
            .andExpect(status().isOk())
            .andReturn();
        SimpleUserDto userResponse = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), SimpleUserDto.class);

        assertAll(
            () -> assertEquals(applicationUser.getEmail(), userResponse.email()),
            () -> assertEquals(applicationUser.getFirstName(), userResponse.firstName()),
            () -> assertEquals(applicationUser.getLastName(), userResponse.lastName()),
            () -> assertEquals(applicationUser.getRole().name(), userResponse.role())
        );

    }

    @Test
    public void updateNonExistingUserReturns404() throws Exception {

        String updatedUserJson = new ObjectMapper().writeValueAsString(applicationAdmin);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(User_BASE_URI + "/{email}", "user@example.com")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
            .andExpect(status().isNotFound())
            .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        assertEquals("Could not find hello@world to update", responseContent);

    }


    @Test
    public void updateValidUserWithInvalidPropertiesReturns400() throws Exception {
        userRepository.save(applicationAdmin);
        userRepository.save(applicationUser);
        applicationUser.setFirstName("  ");
        applicationUser.setLastName("  ");
        String updatedUserJson = new ObjectMapper().writeValueAsString(applicationUser);

        MvcResult mvcResult = this.mockMvc.perform(put(User_BASE_URI + "/{email}", applicationUser.getEmail())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
            .andExpect(status().isBadRequest())
            .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        System.out.println(response);
        assertAll(
            () -> assertTrue(response.contains("firstName Firstname must not be blank")),
            () -> assertTrue(response.contains("lastName Lastname must not be blank"))
        );
    }

    @Test
    public void deleteValidUserByMailDeletesUserFromDatabase() throws Exception {
        userRepository.save(applicationUser);
        assertNotNull(userRepository.findByEmail(applicationUser.getEmail()));
        mockMvc.perform(MockMvcRequestBuilders.delete(User_BASE_URI + "/{email}", applicationUser.getEmail())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        assertTrue(userRepository.findByEmail(applicationUser.getEmail()).isEmpty());
    }

    @Test
    public void deleteValidUserByMailTwiceReturns404() throws Exception {
        userRepository.save(applicationUser);
        assertNotNull(userRepository.findByEmail(applicationUser.getEmail()));
        mockMvc.perform(MockMvcRequestBuilders.delete(User_BASE_URI + "/{email}", applicationUser.getEmail())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        assertTrue(userRepository.findByEmail(applicationUser.getEmail()).isEmpty());
        mockMvc.perform(MockMvcRequestBuilders.delete(User_BASE_URI + "/{email}", applicationUser.getEmail())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteNonExistingUserReturns404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(User_BASE_URI + "/{email}", "non@existing")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void findUserByNameFindsUser() throws Exception {
        userRepository.save(applicationAdmin);
        userRepository.save(applicationUser);

        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.setFirstInput(TEST_USER_FIRSTNAME);

        MvcResult mvcResult = this.mockMvc.perform(get(User_BASE_URI + "/byName")
                .param("firstInput", userSearchDto.getFirstInput())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        List<DetailUserDto> userList = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, userList.size());

    }

    @Test
    public void findNonExistingUserByNameReturnsEmptyArray() throws Exception {
        userRepository.save(applicationAdmin);
        userRepository.save(applicationUser);

        UserSearchDto userSearchDto = new UserSearchDto();
        userSearchDto.setFirstInput("non_existing");

        MvcResult mvcResult = this.mockMvc.perform(get(User_BASE_URI + "/byName")
                .param("firstInput", userSearchDto.getFirstInput())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        List<DetailUserDto> userList = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(0, userList.size());

    }

    @Test
    public void adminUpdateUserToManagerUpdatesRole() throws Exception {
        userRepository.save(applicationUser);
        MvcResult mvcResult = this.mockMvc.perform(put(User_BASE_URI + "/update-role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(applicationUser.getEmail())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        SimpleUserDto simpleUser = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(Role.MANAGER.name(), simpleUser.role());
    }

    @Test
    public void adminUpdateManagerToManagerThrows() throws Exception {
        userRepository.save(applicationManager);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            MvcResult mvcResult = this.mockMvc.perform(put(User_BASE_URI + "/update-role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(applicationManager.getEmail())
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
                .andReturn();
            throw Objects.requireNonNull(mvcResult.getResolvedException());
        });

        assertAll(
            () -> assertTrue(exception.getCause().getCause() instanceof DataIntegrityViolationException),
            () -> assertTrue(exception.getCause().getCause().getMessage().contains("User has already role MANAGER"))
        );
    }


    @Test
    public void managerUpdateUserToManagerReturns403() throws Exception {
        userRepository.save(applicationUser);
        this.mockMvc.perform(put(User_BASE_URI + "/update-role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(applicationAdmin.getEmail())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_MANAGER_EMAIL, MANAGER_ROLES)))
            .andExpect(status().isForbidden())
            .andReturn();
    }

    @Test
    public void adminFindAllUsersFindsAllUsers() throws Exception {
        userRepository.save(applicationManager);
        userRepository.save(applicationUser);
        MvcResult mvcResult = this.mockMvc.perform(get(User_BASE_URI + "/all")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<SimpleUserDto> userList = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
            () -> assertEquals(2, userList.size()),
            () -> assertEquals(TEST_MANAGER_EMAIL, userList.get(0).email()),
            () -> assertEquals(TEST_USER_EMAIL + ".at", userList.get(1).email())
        );

    }

    @Test
    public void userFindAllUsersReturns403() throws Exception {
        userRepository.save(applicationManager);
        userRepository.save(applicationUser);
        this.mockMvc.perform(get(User_BASE_URI + "/all")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isForbidden())
            .andReturn();
    }


    @Test
    public void getCurrentNonExistingUserReturns404() throws Exception {
        userRepository.save(applicationManager);
        userRepository.save(applicationUser);
        this.mockMvc.perform(get(User_BASE_URI + "/all")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isForbidden())
            .andReturn();
    }

    @Test
    public void managerRemoveNonExistingUserFromDepartmentReturns404() throws Exception {
        this.mockMvc.perform(put(User_BASE_URI + "/removeFromDepartment/{email}", applicationUser.getEmail())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_USER_EMAIL, MANAGER_ROLES)))
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    public void userRemoveUserFromDepartmentReturns403() throws Exception {
        userRepository.save(applicationUser);
        this.mockMvc.perform(put(User_BASE_URI + "/removeFromDepartment/{email}", TEST_USER_EMAIL)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_USER_EMAIL, USER_ROLES)))
            .andExpect(status().isForbidden())
            .andReturn();
    }
}
