package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import org.json.JSONObject;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class RegisterEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryInterface userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private DatabaseCleaner databaseCleaner;


    String User_BASE_URI = BASE_URI + "/register";

    private ApplicationUser applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()

        .withFirstName(TEST_USER_FIRSTNAME)
        .withLastName(TEST_USER_LASTNAME)
        .withEmail(TEST_USER_EMAIL)
        .withPassword(TEST_USER_PASSWORD)
        .withRole(Role.USER)
        .build();


    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();
        applicationUser = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withFirstName(TEST_USER_FIRSTNAME)
            .withLastName(TEST_USER_LASTNAME)
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .withRole(Role.USER)
            .build();
    }

    @Test
    public void registerValidUserInsertsIntoDatabase() throws Exception {

        UserRegisterDto userRegisterDto = userMapper.applicationUserToRegisterUserDto(applicationUser);
        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.put(User_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        JSONObject jsonResponse = new JSONObject(content);
        String email = jsonResponse.getString("email");

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> {
                Optional<ApplicationUser> savedUser = userRepository.findByEmail(email);
                assertTrue(savedUser.isPresent());
                ApplicationUser retrievedUser = savedUser.get();
                assertEquals(applicationUser.getFirstName(), retrievedUser.getFirstName());
                assertEquals(applicationUser.getLastName(), retrievedUser.getLastName());
                assertEquals(Role.USER, retrievedUser.getRole());
            }
        );
    }


    @Test
    public void registerInvalidUserThrows400() throws Exception {
        applicationUser.setFirstName("  ");

        UserRegisterDto userRegisterDto = userMapper.applicationUserToRegisterUserDto(applicationUser);
        String body = objectMapper.writeValueAsString(userRegisterDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.put(User_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
                String[] errors = content.split(",");
                assertEquals(1, errors.length);
            }
        );
    }

    @Test
    public void registerUserTwiceThrows400() throws Exception {
        // Register the user for the first time
        UserRegisterDto userRegisterDto = userMapper.applicationUserToRegisterUserDto(applicationUser);
        String body = objectMapper.writeValueAsString(userRegisterDto);

        this.mockMvc.perform(MockMvcRequestBuilders.put(User_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andExpect(status().isOk());

        // Attempt to register the same user again
        MvcResult secondRegistrationResult = this.mockMvc.perform(MockMvcRequestBuilders.put(User_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse secondRegistrationResponse = secondRegistrationResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), secondRegistrationResponse.getStatus()),
            () -> {
                String content = secondRegistrationResponse.getContentAsString();
                assertNotNull(content, "Response body should not be null");
                String expectedErrorMessage = "Email already registered";
                assertTrue(content.contains(expectedErrorMessage),
                    "Expected error message '" + expectedErrorMessage + "' not found in the response body");
            }
        );
    }
}