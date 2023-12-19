package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSuggestionsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping("byEmail/{email}")
    @Operation(summary = "Get information of user by their email", security = @SecurityRequirement(name = "apiKey"))
    public SimpleUserDto findByEmail(@PathVariable String email) {
        LOGGER.info("GET /api/v1/user/byEmail/" + email);
        ApplicationUser user = userService.findUserByEmail(email);
        return userMapper.applicationUserToDetailedUserDto(user);
    }

    @Secured("ROLE_USER")
    @PutMapping(value = "{email}")
    @Operation(summary = "Update a User", security = @SecurityRequirement(name = "apiKey"))
    public SimpleUserDto updateUser(@Valid @RequestBody SimpleUserDto user) {
        LOGGER.info("GET /api/v1/user/" + user);
        try {
            return userService.update(user);
        } catch (DataIntegrityViolationException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @PermitAll
    @DeleteMapping("/{userToDelete}")
    @Operation(summary = "Delete User", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@Valid @PathVariable String userToDelete) {
        LOGGER.info("DELETE /api/v1/delete/userToDelete: {}", userToDelete);
        System.out.println("delete" + userToDelete);
        try {
            userService.delete(userToDelete);
        } catch (DataIntegrityViolationException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @GetMapping("/byName")
    @Operation(summary = "Get users by name", security = @SecurityRequirement(name = "apiKey"))
    public List<DetailUserDto> findUserByName(UserSearchDto userSearchDto) {
        LOGGER.info("GET /api/v1/user/byName" + userSearchDto);
        LOGGER.info("Search parameters: {}", userSearchDto);
        return userMapper.applicationUserToDetailUserDto(userService.findUserByName(userSearchDto));
    }

    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @GetMapping("/suggestions")
    @Operation(summary = "Get project member suggestions by name", security = @SecurityRequirement(name = "apiKey"))
    public List<DetailUserDto> findSuggestionsByName(UserSuggestionsDto userSuggestionsDto) {
        LOGGER.info("GET /api/v1/user/byName" + userSuggestionsDto);
        LOGGER.info("Search parameters: {}", userSuggestionsDto);
        return userMapper.applicationUserToDetailUserDto(userService.findSuggestionsByName(userSuggestionsDto));
    }

    @Deprecated
    @Secured("ROLE_ADMIN")
    @PutMapping(value = "/update-role")
    @Operation(summary = "Update User Role", security = @SecurityRequirement(name = "apiKey"))
    public SimpleUserDto updateRole(@Valid @RequestBody String username) {
        LOGGER.info("PUT /api/v1/user/update-role body: {}", username);
        return userService.upgradeUserToManager(username);
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @GetMapping("/all")
    @Operation(summary = "Get All Users.", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleUserDto> findAllUsers() {
        LOGGER.info("GET /api/v1/user/all");
        return userService.findAllUsers();
    }

    @Secured("ROLE_MANAGER")
    @PutMapping("/inviteToDepartment")
    @Operation(summary = "Invite user to department", security = @SecurityRequirement(name = "apiKey"))
    public void inviteToDepartment(@RequestBody String username) {
        LOGGER.info("PUT /api/v1/user/inviteToDepartment body: {}", username);
        SimpleUserDto manager = getCurrentUser();
        userService.inviteToDepartment(manager, username);
    }

    //@Secured("ROLE_USER")
    @PermitAll
    @PutMapping("/departmentInvite/{manager}/{token}/{user}")
    @Operation(summary = "Add user to department of manager", security = @SecurityRequirement(name = "apiKey"))
    public void addToDepartment(@PathVariable String manager, @PathVariable String token, @PathVariable String user) {
        LOGGER.info("PUT /api/v1/user/departmentInvite body: {}", user + " to " + manager + " with " + token);
        userService.addToDepartment(manager, user, token);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "current")
    @Operation(summary = "Get information of the currently logged-in manager", security = @SecurityRequirement(name = "apiKey"))
    private SimpleUserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String managerEmail = authentication.getName();

        LOGGER.info("GET /api/v1/user/current");
        ApplicationUser manager = userService.findUserByEmail(managerEmail);
        return userMapper.applicationUserToDetailedUserDto(manager);
    }

    // remove user out of department by setting department to null
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @PutMapping(value = "removeFromDepartment/{email}")
    public void removeUserFromDepartment(@PathVariable String email) {
        LOGGER.info("PUT /api/v1/user/removeFromDepartment/" + email);
        userService.removeUserFromDepartment(email);
    }

}
