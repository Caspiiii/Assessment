package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailProjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ProjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.repository.DepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ProjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class ProjectEndpointTest implements TestData {

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
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;


    String Projects_BASE_URI = BASE_URI + "/projects";

    private ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
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
    }

    @Test
    public void createValidProjectReturnsCorrectProject() throws Exception {
        var savedUser = userRepository.save(applicationUser);
        List list = new ArrayList<>();
        departmentService.createDepartment("hi", savedUser.getEmail(), list);

        ProjectCreateDto projectCreateDto = ProjectCreateDto.ProjectCreateDtoBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .withManagerEmail(savedUser.getEmail())
            .build();

        String projectCreateDtoJson = new ObjectMapper().writeValueAsString(projectCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(post(Projects_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectCreateDtoJson))
            .andExpect(status().isCreated())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        DetailProjectDto projectDto = objectMapper.readValue(content, DetailProjectDto.class);
        assertEquals(project.getName(), projectDto.getName());
        assertEquals(project.getMembers(), projectDto.getMembers());
    }

    @Test
    public void getProjectReturnsCorrectProject() throws Exception {
        Project project = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .build();

        project = projectRepository.save(project);

        MvcResult mvcResult = this.mockMvc.perform(get(Projects_BASE_URI + "/" + project.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andExpect(status().isOk())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        DetailProjectDto projectDto = objectMapper.readValue(content, DetailProjectDto.class);
        assertEquals(project.getName(), projectDto.getName());
        assertEquals(new ArrayList<>(), projectDto.getMembers());
    }

    @Test
    public void updateProjectReturnsUpdatedProject() throws Exception {
        Project project = Project.ProjectBuilder.aDefaultProject()
            .withName(TEST_PROJECT_NAME)
            .build();

        project = projectRepository.save(project);

        DetailProjectDto detailProjectDto = DetailProjectDto.DetailProjectDtoBuilder.aDefaultProject()
            .withId(project.getId())
            .withName("new name")
            .withMembers(new ArrayList<>())
            .build();

        String detailProjectDtoJson = new ObjectMapper().writeValueAsString(detailProjectDto);

        MvcResult mvcResult = this.mockMvc.perform(put(Projects_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(detailProjectDtoJson))
            .andExpect(status().isOk())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        DetailProjectDto projectDto = objectMapper.readValue(content, DetailProjectDto.class);
        assertEquals("new name", projectDto.getName());
        assertEquals(new ArrayList<>(), projectDto.getMembers());
    }

}
