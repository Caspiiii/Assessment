package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.DatabaseCleaner;
import at.ac.tuwien.sepr.groupphase.backend.TestConfig;
import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class LoginEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryInterface userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private DatabaseCleaner databaseCleaner;
    String LOGIN_BASE_URI = BASE_URI + "/authentication";
    private ApplicationUser applicationManager = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withFirstName(TEST_MANAGER_FIRSTNAME)
        .withLastName(TEST_MANAGER_LASTNAME)
        .withEmail("manager1@email.com")
        .withPassword("$2a$10$/9QuBk6UMfHeeGpIvGsNW.yO/4ky834ISNqwUiPnrLlsfuKd.cada")
        .withRole(Role.MANAGER)
        .build();


    @BeforeEach
    public void beforeEach() throws SQLException {
        databaseCleaner.cleanDatabase();
        userRepository.save(applicationManager);
    }

    @Test
    public void validLoginReturnsBearer() throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("manager1@email.com");
        userLoginDto.setPassword("password");
        String body = objectMapper.writeValueAsString(userLoginDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertNotNull(response.getContentAsString()),
            () -> assertTrue(response.getContentAsString().contains("Bearer"))
        );
    }
    @Test
    public void invalidLoginThrows() throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("manager1@email.com");
        userLoginDto.setPassword("notpassword");
        String body = objectMapper.writeValueAsString(userLoginDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBody = response.getContentAsString();

        assertAll(
            () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus()),
            () -> assertTrue(responseBody.contains("Username or password is incorrect"))
        );
    }
}
