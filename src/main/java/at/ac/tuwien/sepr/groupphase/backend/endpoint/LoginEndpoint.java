package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.AdminUserGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.AnswerDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.DepartmentDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.datagenerator.QuestionDataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/v1/authentication", method = {RequestMethod.GET, RequestMethod.POST})
public class LoginEndpoint {

    private final UserService userService;


    @Autowired
    private AdminUserGenerator adminUserGenerator;

    @Autowired
    private QuestionDataGenerator questionDataGenerator;

    @Autowired
    private DepartmentDataGenerator departmentDataGenerator;

    @Autowired
    private AnswerDataGenerator answerDataGenerator;

    public LoginEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @CrossOrigin(origins = "http://3.122.112.34")
    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto) {
        try {
            return userService.login(userLoginDto);
        } catch (BadCredentialsException e) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }

    }
}
