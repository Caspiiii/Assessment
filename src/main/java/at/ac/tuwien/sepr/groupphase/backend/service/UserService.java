package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdatePasswordRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSuggestionsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findUserByEmail(String email);

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto);

    /**
     * Deletes a user.
     *
     * @param userDeleteDto login credentials
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    void delete(String userDeleteDto);

    /**
     * Register a new user.
     *
     * @param userRegisterDto data for a new user
     * @return the added User as DetailUserDto
     */
    SimpleUserDto register(UserRegisterDto userRegisterDto);

    /**
     * Update an existing User.
     *
     * @param user data of the user to update
     * @return the data of the updated User
     */
    SimpleUserDto update(SimpleUserDto user);

    /**
     * Search a Users that have a matching first and last name.
     *
     * @param userSearchDto the search terms
     * @return a list of users that match the search terms
     */
    List<ApplicationUser> findUserByName(UserSearchDto userSearchDto);

    /**
     * Search a Users that have a matching first and last name and are not already part of the specified project.
     *
     * @param userSuggestionsDto the search terms and the project id
     * @return a list of users that match the search terms
     */
    public List<ApplicationUser> findSuggestionsByName(UserSuggestionsDto userSuggestionsDto);

    /**
     * Send email to reset password.
     *
     * @param email to send to
     * @return token
     */
    String resetPassword(String email);

    /**
     * updates password.
     *
     * @param updatePasswordRequest token and password to set
     * @return true if password updated else false
     * @throws at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException if no valid token found
     */
    boolean updatePassword(UpdatePasswordRequest updatePasswordRequest);

    /**
     * Upgrade a user to manager.
     *
     * @param username the username of the user to upgrade
     * @return the upgraded user
     */
    SimpleUserDto upgradeUserToManager(String username);

    /**
     * Find all users.
     *
     * @return a list of users
     */
    List<SimpleUserDto> findAllUsers();

    /**
     * Send invite email to user.
     *
     * @param manager manager user
     * @param email email of user
     * @return token if invited else null
     */
    public String inviteToDepartment(SimpleUserDto manager, String email);

    /**
     * Add user to group(member).
     *
     * @param email email of user
     * @param token token
     * @throws at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException if no valid token found
     */
    void addToDepartment(String manager, String email, String token);

    /**
     * Remove user from department.
     *
     * @param email email of user
     */
    void removeUserFromDepartment(String email);
}
