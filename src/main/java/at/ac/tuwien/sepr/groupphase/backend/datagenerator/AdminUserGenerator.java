package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

//@Profile({"generateData", "withAnswers"})
@Component
public class AdminUserGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ADMIN_EMAIL = "admin@email.com";

    @Autowired
    private DataSource dataSource;

    private final UserRepositoryInterface userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserGenerator(UserRepositoryInterface userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void generateAdmin() {
        try {
            cleanupDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
            LOGGER.debug("Admin user already exists");
        } else {
            LOGGER.debug("generating {} admin user", ADMIN_EMAIL);
            ApplicationUser admin = new ApplicationUser("Admin", "Admin", ADMIN_EMAIL, passwordEncoder.encode("password"), Role.ADMIN);
            LOGGER.debug("saving admin user {}", ADMIN_EMAIL);
            userRepository.save(admin);
        }
        if (!userRepository.findByEmail("manager@email.com").isPresent()) {
            ApplicationUser manager = new ApplicationUser("Manager", "Manager", "manager@email.com", passwordEncoder.encode("password"), Role.MANAGER);
            userRepository.save(manager);
        }
        if (!userRepository.findByEmail("manager1@email.com").isPresent()) {
            ApplicationUser managerUser1 = new ApplicationUser("Manager", "1", "manager1@email.com", passwordEncoder.encode("password"), Role.MANAGER);
            userRepository.save(managerUser1);
        }
        if (!userRepository.findByEmail("manager2@email.com").isPresent()) {
            ApplicationUser managerUser2 = new ApplicationUser("Manager", "2", "manager2@email.com", passwordEncoder.encode("password"), Role.MANAGER);
            userRepository.save(managerUser2);
        }
        if (!userRepository.findByEmail("manager3@email.com").isPresent()) {
            ApplicationUser managerUser3 = new ApplicationUser("Manager", "3", "manager3@email.com", passwordEncoder.encode("password"), Role.MANAGER);
            userRepository.save(managerUser3);
        }
        if (!userRepository.findByEmail("manager4@email.com").isPresent()) {
            ApplicationUser managerUser4 = new ApplicationUser("Manager", "4", "manager4@email.com", passwordEncoder.encode("password"), Role.MANAGER);
            userRepository.save(managerUser4);
        }

        if (!userRepository.findByEmail("daniela@email.com").isPresent()) {
            ApplicationUser user1 = new ApplicationUser("daniela", "User1", "daniela@email.com", passwordEncoder.encode("password"), Role.USER);
            userRepository.save(user1);
        }
        if (!userRepository.findByEmail("caspian@email.com").isPresent()) {
            ApplicationUser user1 = new ApplicationUser("caspian", "User2", "caspian@email.com", passwordEncoder.encode("password"), Role.USER);
            userRepository.save(user1);
        }
        if (!userRepository.findByEmail("elsa@email.com").isPresent()) {
            ApplicationUser user1 = new ApplicationUser("elsa", "User3", "elsa@email.com", passwordEncoder.encode("password"), Role.USER);
            userRepository.save(user1);
        }
        if (!userRepository.findByEmail("fabian@email.com").isPresent()) {
            ApplicationUser user1 = new ApplicationUser("fabian", "User4", "fabian@email.com", passwordEncoder.encode("password"), Role.USER);
            userRepository.save(user1);
        }
        if (!userRepository.findByEmail("franz@email.com").isPresent()) {
            ApplicationUser user1 = new ApplicationUser("franz", "User5", "franz@email.com", passwordEncoder.encode("password"), Role.USER);
            userRepository.save(user1);
        }
        if (!userRepository.findByEmail("luca@email.com").isPresent()) {
            ApplicationUser user1 = new ApplicationUser("luca", "User6", "luca@email.com", passwordEncoder.encode("password"), Role.USER);
            userRepository.save(user1);
        }
        if (!userRepository.findByEmail("user1@email.com").isPresent()) {
            ApplicationUser user1 = new ApplicationUser("User1", "NoDepartment", "user1@email.com", passwordEncoder.encode("password"), Role.USER);
            userRepository.save(user1);
        }
        if (!userRepository.findByEmail("user2@email.com").isPresent()) {
            ApplicationUser user1 = new ApplicationUser("User2", "NoDepartment", "user2@email.com", passwordEncoder.encode("password"), Role.USER);
            userRepository.save(user1);
        }
        if (!userRepository.findByEmail("manager@email.com").isPresent()) {
            ApplicationUser user1 = new ApplicationUser("manager", "Mustermann", "manager@email.com", passwordEncoder.encode("password"), Role.MANAGER);
            userRepository.save(user1);
        }

    }

    private void cleanupDatabase() throws SQLException {
        Connection c = dataSource.getConnection();
        Statement s = c.createStatement();
        //s.executeUpdate("DROP ALL OBJECTS");

        // Disable FK
        s.execute("SET REFERENTIAL_INTEGRITY FALSE");

        // Find all tables and truncate them
        Set<String> tables = new HashSet<>();
        ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        rs.close();
        for (String table : tables) {
            s.executeUpdate("DELETE FROM " + table);
        }

        /*// Idem for sequences
        Set<String> sequences = new HashSet<>();
        rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
        while (rs.next()) {
            sequences.add(rs.getString(1));
        }
        rs.close();
        for (String seq : sequences) {
            s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
        }
        */
        // Enable FK
        s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        s.close();
        c.close();
    }
}
