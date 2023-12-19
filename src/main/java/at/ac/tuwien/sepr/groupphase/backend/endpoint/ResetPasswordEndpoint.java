package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdatePasswordRequest;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;


@RestController
@RequestMapping(value = "/api/v1/resetPassword")
public class ResetPasswordEndpoint {

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ResetPasswordEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @PutMapping
    public void resetUserPassword(@Valid @RequestBody String email) {
        LOGGER.info("Put /api/v1/resetPassword body: {}", email);
        String emailAdress = email.substring(10, email.length() - 2);
        userService.resetPassword(emailAdress);
    }

    @PermitAll
    @PostMapping("/updatePassword")
    public void updatePassword(@RequestBody UpdatePasswordRequest request) {
        LOGGER.info("Post /api/v1/resetPassword/updatePassword body: {}", request);
        userService.updatePassword(request);
    }
}