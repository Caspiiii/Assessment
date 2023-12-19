package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.repository.DepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ProjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.service.DepartmentService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

//@Profile({"generateData", "withAnswers"})
@Component
public class DepartmentDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DepartmentRepository departmentRepository;

    @Autowired
    private AdminUserGenerator adminUserGenerator;

    private final UserRepositoryInterface userRepository;
    private final DepartmentService departmentService;

    private final ProjectRepository projectRepository;

    public DepartmentDataGenerator(DepartmentRepository departmentRepository, UserRepositoryInterface userRepository, DepartmentService departmentService,
                                   ProjectRepository projectRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.departmentService = departmentService;
        this.projectRepository = projectRepository;
    }


    @PostConstruct
    private void generateDepartment() {

        List<Project> projects = projectRepository.findAll();
        List<ApplicationUser> members = userRepository.findAll();
        Optional<ApplicationUser> manager = userRepository.findByEmail("manager1@email.com");
        ApplicationUser manager1 = null;
        if (manager.isPresent()) {
            manager1 = manager.get();
        }
        Iterator<ApplicationUser> iter = members.iterator();
        List<ApplicationUser> dep1 = new ArrayList<>();
        List<Project> depProject1 = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getRole() == Role.USER) {
                dep1.add(members.get(i));
            }
        }
        /*
        Department department1 = new Department("Department 1", manager1, dep1, depProject1);
        departmentRepository.save(department1);

        for (ApplicationUser user : members) {
            if (user.getRole() == Role.USER) {
                user.setDepartment(department1);
                userRepository.save(user);
            }
        }
        List<ApplicationUser> list = departmentRepository.findByManagerEmail("manager1@email.com").get().getMembers();
        for (ApplicationUser l : list) {
            LOGGER.error("{}", l.toString());
        }
        */
        LOGGER.info("generating {} department", "Department1");
        List<ApplicationUser> members1 = new ArrayList<>(3);
        int i = 0;
        while (iter.hasNext() && i < 3) {
            ApplicationUser user = iter.next();
            if (user.getRole() == Role.USER) {
                members1.add(user);
                i++;
            }
        }
        for (ApplicationUser user : members1) {
            LOGGER.info("user {}", user.getEmail());
        }
        LOGGER.info("-------------------");
        DepartmentDto dto = departmentService.createDepartment("Department 1", "manager1@email.com", members1.stream().map(m -> m.getEmail()).toList());
        LOGGER.info("Saved department: " + dto.name() + " with id: " + dto.id() + " and manager: " + dto.manager().email());
        for (SimpleUserDto member : dto.members()) {
            LOGGER.info("user {}", member.email());
        }
        LOGGER.info("-------------------");
        members1 = new ArrayList<>(3);
        i = 0;
        while (iter.hasNext() && i < 2) {
            ApplicationUser user = iter.next();
            if (user.getRole() == Role.USER) {
                members1.add(user);
                i++;
            }
        }
        for (ApplicationUser user : members1) {
            LOGGER.info("user {}", user.getEmail());
        }
        dto = departmentService.createDepartment("Department 2", "manager2@email.com", members1.stream().map(m -> m.getEmail()).toList());
        LOGGER.info("Saved department: " + dto.name() + " with id: " + dto.id() + " and manager: " + dto.manager().email());
        for (SimpleUserDto member : dto.members()) {
            LOGGER.info("user {}", member.email());
        }
        LOGGER.info("-------------------");
        LOGGER.info("generating {} department", "Department3");
        members1 = new ArrayList<>(3);
        i = 0;
        while (iter.hasNext() && i < 1) {
            ApplicationUser user = iter.next();
            if (user.getRole() == Role.USER) {
                members1.add(user);
                i++;
            }
        }
        dto = departmentService.createDepartment("Department 3", "manager3@email.com", members1.stream().map(m -> m.getEmail()).toList());
        LOGGER.info("Saved department: " + dto.name() + " with id: " + dto.id() + " and manager: " + dto.manager().email());
        for (SimpleUserDto member : dto.members()) {
            LOGGER.info("user {}", member.email());
        }
        LOGGER.info("-------------------");
    }
}
