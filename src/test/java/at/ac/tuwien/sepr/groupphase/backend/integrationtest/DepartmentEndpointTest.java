package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DepartmentCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.repository.DepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class DepartmentEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepositoryInterface userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private UserMapper userMapper;
    String DEPARTMENT_BASE_URI = BASE_URI + "/department";

    private SimpleUserDto user = new SimpleUserDto(TEST_USER_FIRSTNAME, TEST_USER_LASTNAME,
        TEST_USER_EMAIL, TEST_USER_PASSWORD, Role.USER.toString(), null);
    private SimpleUserDto manager = new SimpleUserDto(TEST_MANAGER_FIRSTNAME, TEST_MANAGER_LASTNAME,
        TEST_MANAGER_EMAIL, TEST_MANAGER_PASSWORD, Role.MANAGER.toString(), null);

    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();
        userRepository.save(userMapper.simpleUserDtoToApplicationUser(user));
        userRepository.save(userMapper.simpleUserDtoToApplicationUser(manager));
    }

    @Test
    public void createValidDepartmentInsertsIntoDatabase() throws Exception {
        List<String> members = new LinkedList<>();
        members.add(TEST_USER_EMAIL);
        DepartmentCreateDto departmentCreateDto = new DepartmentCreateDto(TEST_DEPARTMENT_NAME, TEST_MANAGER_EMAIL, members);
        String body = objectMapper.writeValueAsString(departmentCreateDto);


        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        DepartmentDto departmentDto = objectMapper.readValue(content, DepartmentDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(TEST_DEPARTMENT_NAME, departmentDto.name()),
            () -> assertEquals(TEST_MANAGER_EMAIL, departmentDto.manager().email()),
            () -> assertEquals(1, departmentDto.members().size()),
            () -> assertEquals(TEST_USER_EMAIL, departmentDto.members().get(0).email())
        );
    }

    @Test
    public void createDepartmentWithoutMemberCreatesDepartment() throws Exception {
        DepartmentCreateDto departmentCreateDto = new DepartmentCreateDto(TEST_DEPARTMENT_NAME, TEST_MANAGER_EMAIL, null);
        String body = objectMapper.writeValueAsString(departmentCreateDto);


        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        DepartmentDto departmentDto = objectMapper.readValue(content, DepartmentDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(TEST_DEPARTMENT_NAME, departmentDto.name()),
            () -> assertEquals(TEST_MANAGER_EMAIL, departmentDto.manager().email()),
            () -> assertEquals(0, departmentDto.members().size())
        );
    }



    @Test
    public void createDepartmentWithoutManagerReturnsValidationErrors() throws Exception {
        List<String> members = new LinkedList<>();
        members.add(TEST_USER_EMAIL);
        DepartmentCreateDto departmentCreateDto = new DepartmentCreateDto(TEST_DEPARTMENT_NAME, null, members);
        String body = objectMapper.writeValueAsString(departmentCreateDto);


        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBody = response.getContentAsString();
        assertAll(
            () -> assertEquals(400, response.getStatus()),
            () -> assertTrue(responseBody.contains("Validation errors")),
            () -> assertTrue(responseBody.contains("Manager email must not be null"))
        );
    }


    @Test
    public void createDepartmentWithBlankNameReturnsValidationErrorsWith400() throws Exception {
        List<String> members = new LinkedList<>();
        members.add(TEST_USER_EMAIL);
        DepartmentCreateDto departmentCreateDto = new DepartmentCreateDto("   ", TEST_MANAGER_EMAIL, members);
        String body = objectMapper.writeValueAsString(departmentCreateDto);


        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBody = response.getContentAsString();

        assertAll(
            () -> assertEquals(400, response.getStatus()),
            () -> assertTrue(responseBody.contains("Validation errors")),
            () -> assertTrue(responseBody.contains("name Department name must not be blank"))

        );
    }


    @Test
    public void userCreatingDepartmentReturnsAuthenticationError403() throws Exception {
        List<String> members = new LinkedList<>();
        members.add(TEST_USER_EMAIL);
        DepartmentCreateDto departmentCreateDto = new DepartmentCreateDto(TEST_DEPARTMENT_NAME, TEST_MANAGER_EMAIL, members);
        String body = objectMapper.writeValueAsString(departmentCreateDto);
        this.mockMvc.perform(MockMvcRequestBuilders.post(DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andReturn();
    }

    @Test
    public void adminGetDepartmentsWithEmptyTableReturnsEmptyArray() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(DEPARTMENT_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<DepartmentDto> departments = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertNotNull(departments),
            () -> assertEquals(0, departments.size())
        );
    }

    @Test
    public void managerGetDepartmentsThrowsAuthorizeExceptionAndReturns403() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_MANAGER_EMAIL, MANAGER_ROLES)))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andReturn();
    }


    @Test
    public void adminGetNonExistingDepartmentByIdReturns404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(DEPARTMENT_BASE_URI + "/{id}", 111)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBody = response.getContentAsString();

        assertAll(
            () -> assertEquals(404, response.getStatus()),
            () -> assertTrue(responseBody.contains("Department with id 111 not found"))
        );
    }

    @Test
    public void userGetDepartmentByIdThrowsAuthorizeExceptionReturns403() throws Exception {
        departmentRepository.save(new Department());
        this.mockMvc.perform(MockMvcRequestBuilders.get(DEPARTMENT_BASE_URI + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andReturn();
    }

    @Test
    public void managerUpdatesDepartment() throws Exception {
        List<String> members = new LinkedList<>();
        members.add(TEST_USER_EMAIL);
        DepartmentCreateDto departmentCreateDto = new DepartmentCreateDto(TEST_DEPARTMENT_NAME, TEST_MANAGER_EMAIL, members);
        String body = objectMapper.writeValueAsString(departmentCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        DepartmentDto departmentDto = objectMapper.readValue(content, DepartmentDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(TEST_DEPARTMENT_NAME, departmentDto.name()),
            () -> assertEquals(TEST_MANAGER_EMAIL, departmentDto.manager().email()),
            () -> assertEquals(1, departmentDto.members().size()),
            () -> assertEquals(TEST_USER_EMAIL, departmentDto.members().get(0).email())
        );

        SimpleUserDto manager = new SimpleUserDto(TEST_MANAGER_FIRSTNAME, TEST_MANAGER_LASTNAME,
            TEST_MANAGER_EMAIL, TEST_MANAGER_PASSWORD, Role.MANAGER.toString(), departmentDto.name());
        List<SimpleUserDto> membersDto = Arrays.asList(new SimpleUserDto(TEST_USER_FIRSTNAME, TEST_USER_LASTNAME,
            TEST_USER_EMAIL, TEST_USER_PASSWORD, Role.USER.toString(), departmentDto.name()));
        DepartmentDto departmentUpdateDto = new DepartmentDto(departmentDto.id(), "new name", manager, membersDto, null);
        String updateBody = objectMapper.writeValueAsString(departmentUpdateDto);

        MvcResult updateResult = this.mockMvc.perform(MockMvcRequestBuilders.put(DEPARTMENT_BASE_URI + "/{id}", departmentDto.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_MANAGER_EMAIL, MANAGER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse updateResponse = updateResult.getResponse();
        String updateContent = updateResponse.getContentAsString();
        DepartmentDto updatedDepartmentDto = objectMapper.readValue(updateContent, DepartmentDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), updateResponse.getStatus()),
            () -> assertEquals("new name", updatedDepartmentDto.name()),
            () -> assertEquals(TEST_MANAGER_EMAIL, updatedDepartmentDto.manager().email()),
            () -> assertEquals(1, updatedDepartmentDto.members().size()),
            () -> assertEquals(TEST_USER_EMAIL, updatedDepartmentDto.members().get(0).email())
        );
    }

    @Test
    public void userUpdatesDepartmentReturns403() throws Exception{
        SimpleUserDto user = new SimpleUserDto(TEST_USER_FIRSTNAME, TEST_USER_LASTNAME,
            TEST_USER_EMAIL, TEST_USER_PASSWORD, Role.USER.toString(), TEST_DEPARTMENT_NAME);
        List<SimpleUserDto> membersDto = Arrays.asList(user);
        DepartmentDto departmentUpdateDto = new DepartmentDto(1L, "new name", user, membersDto, null);
        String updateBody = objectMapper.writeValueAsString(departmentUpdateDto);

            MvcResult updateResult = this.mockMvc.perform(MockMvcRequestBuilders.put(DEPARTMENT_BASE_URI + "/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateBody)
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_USER_EMAIL, USER_ROLES)))
                .andDo(print())
                .andReturn();
            MockHttpServletResponse updateResponse = updateResult.getResponse();
            assertAll(
                () -> assertEquals(HttpStatus.FORBIDDEN.value(), updateResponse.getStatus())
            );
    }

    @Test
    public void managerUpdatesDepartmentWithEmptyNameReturns400() throws Exception {
        List<String> members = new LinkedList<>();
        members.add(TEST_USER_EMAIL);
        DepartmentCreateDto departmentCreateDto = new DepartmentCreateDto(TEST_DEPARTMENT_NAME, TEST_MANAGER_EMAIL, members);
        String body = objectMapper.writeValueAsString(departmentCreateDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(DEPARTMENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        DepartmentDto departmentDto = objectMapper.readValue(content, DepartmentDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(TEST_DEPARTMENT_NAME, departmentDto.name()),
            () -> assertEquals(TEST_MANAGER_EMAIL, departmentDto.manager().email()),
            () -> assertEquals(1, departmentDto.members().size()),
            () -> assertEquals(TEST_USER_EMAIL, departmentDto.members().get(0).email())
        );

        SimpleUserDto manager = new SimpleUserDto(TEST_MANAGER_FIRSTNAME, TEST_MANAGER_LASTNAME,
            TEST_MANAGER_EMAIL, TEST_MANAGER_PASSWORD, Role.MANAGER.toString(), departmentDto.name());
        List<SimpleUserDto> membersDto = Arrays.asList(new SimpleUserDto(TEST_USER_FIRSTNAME, TEST_USER_LASTNAME,
            TEST_USER_EMAIL, TEST_USER_PASSWORD, Role.USER.toString(), departmentDto.name()));
        DepartmentDto departmentUpdateDto = new DepartmentDto(departmentDto.id(), "   ", manager, membersDto, null);
        String updateBody = objectMapper.writeValueAsString(departmentUpdateDto);

        MvcResult updateResult = this.mockMvc.perform(MockMvcRequestBuilders.put(DEPARTMENT_BASE_URI + "/{id}", departmentDto.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_MANAGER_EMAIL, MANAGER_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse updateResponse = updateResult.getResponse();
        String updateContent = updateResponse.getContentAsString();
        System.out.println("Content: " + updateContent);

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), updateResponse.getStatus()),
            () -> assertTrue(updateContent.contains("Department name must not be blank"))
        );
    }
}
