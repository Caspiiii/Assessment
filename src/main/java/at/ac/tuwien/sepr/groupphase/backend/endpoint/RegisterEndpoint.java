package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;


@RestController
@RequestMapping(value = "/api/v1/register")
public class RegisterEndpoint {

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public RegisterEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @PutMapping
    public SimpleUserDto register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        LOGGER.info("Put /api/v1/register body: {}", userRegisterDto);
        System.out.println(userRegisterDto.toString());
        try {
            return userService.register(userRegisterDto);
        } catch (DataIntegrityViolationException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }
}