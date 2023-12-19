package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UpdatePasswordRequest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSuggestionsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.DepartmentInviteToken;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetPasswordToken;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.InviteTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ProjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ResetPasswordRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ADMIN_EMAIL = "admin@email.com";
    private final UserRepositoryInterface userRepository;

    private final DepartmentRepository departmentRepository;

    private final InviteTokenRepository inviteTokenRepository;

    private final ResetPasswordRepository resetPasswordRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserMapper userMapper;

    @Autowired
    public CustomUserDetailService(UserRepositoryInterface userRepository, ResetPasswordRepository resetPasswordRepository, PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer, UserMapper userMapper, DepartmentRepository departmentRepository, InviteTokenRepository inviteTokenRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userMapper = userMapper;
        this.resetPasswordRepository = resetPasswordRepository;
        this.departmentRepository = departmentRepository;
        this.inviteTokenRepository = inviteTokenRepository;
        this.projectRepository = projectRepository;

        initialize();
    }

    private void initialize() {
        if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
            LOGGER.debug("Admin user already exists");
        } else {
            LOGGER.debug("generating {} admin user", ADMIN_EMAIL);
            ApplicationUser admin = new ApplicationUser("Admin", "Admin", ADMIN_EMAIL, passwordEncoder.encode("password"), Role.ADMIN);
            LOGGER.debug("saving admin user {}", ADMIN_EMAIL);
            userRepository.save(admin);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (Role.ADMIN == applicationUser.getRole()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else if (Role.MANAGER == applicationUser.getRole()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_MANAGER", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUser user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return user;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        LOGGER.debug("Login with credentials: {}", userLoginDto);
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
        throw new BadCredentialsException("Username or password is incorrect");
    }

    @Transactional
    @Override
    public void delete(String userToDelete) {
        LOGGER.debug("Delete User {}", userToDelete);
        ApplicationUser applicationUser = userRepository.findByEmail(userToDelete).orElse(null);
        Hibernate.initialize(applicationUser);
        if (applicationUser != null) {
            if (applicationUser.getRole() == Role.ADMIN) {
                throw new DataIntegrityViolationException("The Admin cannot be deleted!");
            }
            if (applicationUser.getRole() == Role.MANAGER && departmentRepository.findByManagerEmail(userToDelete).isPresent()) {
                throw new DataIntegrityViolationException("Cannot delete manager, because he has a department");
            }
            if (applicationUser.getDepartment() != null) {
                applicationUser.getDepartment().getMembers().remove(applicationUser);
                departmentRepository.save(applicationUser.getDepartment());
            }
            for (Project project : applicationUser.getProjects()) {
                project.getMembers().remove(applicationUser);
                projectRepository.save(project);
            }

            if (applicationUser.getRole() == Role.MANAGER) {
                Optional<Department> department = departmentRepository.findByManagerEmail(userToDelete);
                Department department1 = null;

                if (department.isPresent()) {
                    department1 = department.get();

                    LOGGER.info("departmentMembers: {}", department1.getName());
                    List<ApplicationUser> applicationUsers = department1.getMembers();
                    for (ApplicationUser user : applicationUsers) {
                        user.setDepartment(null);
                    }
                    department1.setMembers(null);
                    department1.setManager(null);
                    department1.setProjects(null);
                    departmentRepository.save(department1);
                    userRepository.delete(applicationUser);
                    departmentRepository.delete(department1);
                } else {
                    userRepository.delete(applicationUser);
                }
            } else {
                userRepository.delete(applicationUser);
            }
        } else {
            throw new NotFoundException("Email to delete user of not found");
        }
    }

    @Transactional
    public SimpleUserDto register(UserRegisterDto userRegisterDto) {
        LOGGER.debug("Register new User {}", userRegisterDto);
        try {
            ApplicationUser applicationUser = userRepository.findByEmail(userRegisterDto.getUsername()).orElse(null);
            if (applicationUser == null) {
                return this.addUser(userRegisterDto);
            } else {
                throw new DataIntegrityViolationException("Email already registered");
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DataIntegrityViolationException("Email already registered");
        }
    }

    private SimpleUserDto addUser(UserRegisterDto userRegisterDto) {
        LOGGER.debug("Add User {}", userRegisterDto);
        ApplicationUser newUser = new ApplicationUser();
        newUser.setFirstName(userRegisterDto.getFirstName());
        newUser.setLastName(userRegisterDto.getLastName());
        newUser.setEmail(userRegisterDto.getUsername());
        newUser.setEmail(userRegisterDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        if ((userRegisterDto.getMode().equals("USER")) || userRegisterDto.getMode().equals("user")) {
            newUser.setRole(Role.USER);
        } else if ((userRegisterDto.getMode().equals("MANAGER")) || userRegisterDto.getMode().equals("manager")) {
            newUser.setRole(Role.MANAGER);
        } else {
            throw new DataIntegrityViolationException("role empty");
        }

        ApplicationUser savedUser = userRepository.save(newUser);
        return userMapper.applicationUserToDetailedUserDto(savedUser);
    }

    @Transactional
    @Override
    public SimpleUserDto update(SimpleUserDto userToUpdate) {
        LOGGER.debug("Update User {}", userToUpdate);
        try {
            ApplicationUser user = userRepository.findByEmail(userToUpdate.email()).orElse(null);
            if (user == null) {
                throw new NotFoundException(String.format("Could not find %s to update", userToUpdate.email()));
            } else {
                return this.updateUser(user, userToUpdate);
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DataIntegrityViolationException("Email already registered");
        }
    }

    @Transactional
    @Override
    public SimpleUserDto upgradeUserToManager(String username) {
        LOGGER.debug("Upgrade role of user {}", username);
        try {
            Optional<ApplicationUser> opt = userRepository.findByEmail(username);
            if (opt.isPresent()) {
                ApplicationUser user = opt.get();
                if (Role.MANAGER == user.getRole()) {
                    throw new DataIntegrityViolationException("User has already role MANAGER");
                }
                user.setRole(Role.MANAGER);
                return userMapper.applicationUserToDetailedUserDto(userRepository.save(user));
            }
            throw new NotFoundException("Could not find user " + username + " to upgrade");
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DataIntegrityViolationException("Error while upgrading user to MANAGER: " + e.getMessage());
        }
    }


    private SimpleUserDto updateUser(ApplicationUser user, SimpleUserDto userToUpdate) {
        if (Strings.isBlank(userToUpdate.firstName())) {
            throw new IllegalArgumentException("First name must not be blank");
        }
        if (Strings.isBlank(userToUpdate.lastName())) {
            throw new IllegalArgumentException("Last name must not be blank");
        }
        user.setFirstName(userToUpdate.firstName());
        user.setLastName(userToUpdate.lastName());
        user.setEmail(userToUpdate.email());
        user.setRole(Role.valueOf(userToUpdate.role()));
        user = userRepository.save(user);
        return userMapper.applicationUserToDetailedUserDto(user);
    }

    @Override
    @Transactional
    public List<ApplicationUser> findUserByName(UserSearchDto userSearchDto) {
        LOGGER.debug("Find User By Name {}", userSearchDto);
        if (userSearchDto.getFirstInput() == null) {
            userSearchDto.setFirstInput("");
        }
        if (userSearchDto.getLastInput() == null) {
            userSearchDto.setLastInput("");
        }
        if (userSearchDto.getMaxResults() == null) {
            userSearchDto.setMaxResults(Integer.MAX_VALUE);
        }
        try {
            var users = userRepository.findByName(userSearchDto.getFirstInput(), userSearchDto.getLastInput(),
                PageRequest.of(0, userSearchDto.getMaxResults()));

            for (var user : users) {
                if (user.getDepartment() != null) {
                    Hibernate.initialize(user.getDepartment().getProjects());
                    for (var project : user.getDepartment().getProjects()) {
                        Hibernate.initialize(project.getMembers());
                    }
                }
            }

            return users;
        } catch (NotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public List<ApplicationUser> findSuggestionsByName(UserSuggestionsDto userSuggestionsDto) {
        if (userSuggestionsDto.getFirstInput() == null) {
            userSuggestionsDto.setFirstInput("");
        }
        if (userSuggestionsDto.getLastInput() == null) {
            userSuggestionsDto.setLastInput("");
        }
        if (userSuggestionsDto.getMaxResults() == null) {
            userSuggestionsDto.setMaxResults(Integer.MAX_VALUE);
        }
        try {
            var department = departmentRepository.findByManagerEmail(userSuggestionsDto.getManagerEmail()).orElse(null);
            var users = userRepository.findSuggestionsByName(userSuggestionsDto.getFirstInput(),
                userSuggestionsDto.getLastInput(), userSuggestionsDto.getProjectId(), department,
                PageRequest.of(0, userSuggestionsDto.getMaxResults()));


            for (var user : users) {
                if (user.getDepartment() != null) {
                    Hibernate.initialize(user.getDepartment().getProjects());
                    for (var project : user.getDepartment().getProjects()) {
                        Hibernate.initialize(project.getMembers());
                    }
                }
            }

            return users;
        } catch (NotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public String resetPassword(String email) {
        LOGGER.debug("reset password of {}", email);
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        ApplicationUser user = this.findUserByEmail(email);
        if (user != null) {
            ResetPasswordToken existingToken = resetPasswordRepository.findByEmail(email).orElse(null);
            if (existingToken != null) {
                // Wenn bereits ein Eintrag existiert, aktualisiere diesen
                resetPasswordToken = existingToken;
                resetPasswordToken.setToken(generateToken());
                resetPasswordToken.setLocalDateTime(LocalDateTime.now());
            } else {
                // Erstelle einen neuen Eintrag
                resetPasswordToken = new ResetPasswordToken();
                resetPasswordToken.setEmail(email);
                resetPasswordToken.setToken(generateToken());
                resetPasswordToken.setLocalDateTime(LocalDateTime.now());
            }

            final JavaMailSender javaMailSender = getJavaMailSender();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Reset password for you account");
            String resetPasswordLink = "http://localhost:4200/#/resetPassword/" + resetPasswordToken.getToken();
            String htmlContent = "Please click on the link below to set a new password:\n"
                + resetPasswordLink;
            message.setText(htmlContent);
            javaMailSender.send(message);
            resetPasswordRepository.save(resetPasswordToken);
        }
        return resetPasswordToken.getToken();
    }

    private String generateToken() {
        LOGGER.info("Generate token");
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    private JavaMailSender getJavaMailSender() {
        LOGGER.info("Get java mail sender");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("teamallgnmentservice@gmail.com");
        mailSender.setPassword("zlha vtno hrgo ofmj");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Transactional
    @Override
    public boolean updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        LOGGER.debug("Update password {}", updatePasswordRequest);
        String token = updatePasswordRequest.getResetToken();
        String email = resetPasswordRepository.findEmailByToken(token);
        LocalDateTime timeStamp = resetPasswordRepository.findLocalDateTimeByToken(token);
        if (timeStamp != null) {
            long diff = ChronoUnit.MINUTES.between(LocalDateTime.now(), timeStamp);
            if (Math.abs(diff) < 5) {
                ApplicationUser user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
                    userRepository.save(user);
                    resetPasswordRepository.deleteByToken(updatePasswordRequest.getResetToken());
                    return true;
                }
            } else {
                throw new NotFoundException("Token Expired");
            }
        } else {
            throw new NotFoundException("Token not found");
        }
        return false;
    }

    @Override
    public List<SimpleUserDto> findAllUsers() {
        LOGGER.info("Find all users.");
        return userRepository.findAll().stream()
            .map(userMapper::applicationUserToDetailedUserDto)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public String inviteToDepartment(SimpleUserDto manager, String email) {
        if (!departmentRepository.findByManagerEmail(manager.email()).isPresent()) {
            LOGGER.info("Manager " + manager.email() + " has no department");
            throw new NotFoundException("Manager " + manager.email() + " has no department; you cannot send an invitation.");
        }
        ApplicationUser user = this.findUserByEmail(email);
        DepartmentInviteToken token = null;
        if (user != null) {
            final JavaMailSender javaMailSender = getJavaMailSender();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("invite to department by " + manager.firstName() + " " + manager.lastName());
            if (inviteTokenRepository.findByEmail(email).isPresent()) {
                token = inviteTokenRepository.findByEmail(email).orElse(null);
                inviteTokenRepository.deleteByToken(token.getToken());
            }
            token = new DepartmentInviteToken(manager.email(), email, generateToken(), LocalDateTime.now());
            inviteTokenRepository.save(token);
            String resetPasswordLink = "http://localhost:4200/#/" + "departmentInvite/" + manager.email() + "/" + token.getToken() + "/" + email;
            String htmlContent = "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n\n"
                + "You have been invited to join the department by " + manager.firstName() + " " + manager.lastName() + ".\n"
                + "Please click on the link below to accept the invitation:\n\n"
                + resetPasswordLink + "\n\n"
                + "Best regards,\n" + "Your TeamAIlignment Team";
            message.setText(htmlContent);
            javaMailSender.send(message);
        }
        return token.getToken();
    }

    @Transactional
    @Override
    public void addToDepartment(String manager, String email, String token) {
        LOGGER.info("Add user to department. " + "Manager=" + manager + ", User=" + email + ", Token=" + token + "");
        ApplicationUser user = this.findUserByEmail(email);
        if (user != null) {
            DepartmentInviteToken backendToken = inviteTokenRepository.findByManagerAndEmail(manager, email).orElse(null);
            if (backendToken != null && token.equals(backendToken.getToken())) {
                LOGGER.info("Token match");
                Long diff = ChronoUnit.DAYS.between(LocalDateTime.now(), backendToken.getLocalDateTime());
                if (Math.abs(diff) < (21)) {
                    Optional<Department> opt = departmentRepository.findByManagerEmail(manager);
                    if (opt.isPresent()) {
                        Department department = opt.get();
                        user.setDepartment(department);
                        userRepository.save(user);
                        inviteTokenRepository.deleteByToken(token);
                        LOGGER.info("User " + email + " added to department " + department.getName());
                    } else {
                        LOGGER.warn("No department found for manager " + manager);
                        throw new NotFoundException("No department found for manager");
                    }
                } else {
                    LOGGER.warn("Token is not valid anymore (too old)");
                    throw new NotFoundException("Token is not expired");
                }

            } else {
                LOGGER.info("Token doesn't match");
            }
        }
    }

    /**
     * Remove user from department.
     *
     * @param email email of user
     */
    @Transactional
    @Override
    public void removeUserFromDepartment(String email) {
        LOGGER.info("Remove user with email " + email + " from department.");
        ApplicationUser user = this.findUserByEmail(email);
        if (user != null) {
            user.setDepartment(null);
            userRepository.save(user);
            LOGGER.info("User " + email + " removed from department");
        }
    }
}
