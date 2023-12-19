package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailProjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ProjectMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.service.DepartmentService;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DepartmentRepository departmentRepository;
    private final UserRepositoryInterface userRepository;

    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, UserRepositoryInterface userRepositoryInterface, UserMapper userMapper, ProjectMapper projectMapper) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepositoryInterface;
        this.userMapper = userMapper;
        this.projectMapper = projectMapper;
    }

    @Transactional
    @Override
    public DepartmentDto createDepartment(String departmentName, String managerEmail, List<String> userEmails) {
        LOGGER.info("Create department");
        ApplicationUser manager = userRepository.findByEmail(managerEmail)
            .orElseThrow(() -> new NotFoundException("User with email " + managerEmail + " not found"));
        List<ApplicationUser> users = userRepository.findAllByEmailIn(userEmails);
        List<Project> projects = new ArrayList<>();
        Department department = new Department(departmentName, manager, users, projects);
        department = departmentRepository.save(department);
        for (ApplicationUser user : users) {
            user.setDepartment(department);
            userRepository.save(user);
        }
        SimpleUserDto managerDto = userMapper.applicationUserToDetailedUserDto(department.getManager());
        List<SimpleUserDto> members = department.getMembers().stream()
            .map(userMapper::applicationUserToDetailedUserDto)
            .collect(Collectors.toList());
        List<DetailProjectDto> projectsTdo = new ArrayList<>();
        DepartmentDto dto = new DepartmentDto(department.getId(), department.getName(), managerDto, members, projectsTdo);
        return dto;
    }

    @Override
    @Transactional
    public List<DepartmentDto> getAllDepartments() {
        LOGGER.info("Get all departments");
        List<Department> all = departmentRepository.findAll();
        return all.stream()
            .map(department -> {
                SimpleUserDto managerDto = userMapper.applicationUserToDetailedUserDto(department.getManager());
                List<SimpleUserDto> members = department.getMembers().stream()
                    .map(userMapper::applicationUserToDetailedUserDto)
                    .collect(Collectors.toList());
                Hibernate.initialize(department.getProjects());
                var projectsDto = projectMapper.projectToDetailProjectDto(department.getProjects());
                return new DepartmentDto(department.getId(), department.getName(), managerDto, members, projectsDto);
            })
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public DepartmentDto getDepartmentById(Long id) {
        LOGGER.info("Get department by id");
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));
        SimpleUserDto managerDto = userMapper.applicationUserToDetailedUserDto(department.getManager());
        List<SimpleUserDto> members = department.getMembers().stream()
            .map(userMapper::applicationUserToDetailedUserDto)
            .collect(Collectors.toList());
        Hibernate.initialize(department.getProjects());
        var projectsDto = projectMapper.projectToDetailProjectDto(department.getProjects());
        return new DepartmentDto(department.getId(), department.getName(), managerDto, members, projectsDto);
    }

    @Override
    @Transactional
    public DepartmentDto getDepartmentByManagerEmail(String email) {
        LOGGER.info("Get department by manager email");
        Department department = departmentRepository.findByManagerEmail(email)
            .orElseThrow(() -> new NotFoundException("Department with manager email " + email + " not found"));
        SimpleUserDto managerDto = userMapper.applicationUserToDetailedUserDto(department.getManager());
        List<SimpleUserDto> members = department.getMembers().stream()
            .map(userMapper::applicationUserToDetailedUserDto)
            .collect(Collectors.toList());
        return new DepartmentDto(department.getId(), department.getName(), managerDto, members, null);
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long id, String name, String managerEmail, List<String> userEmails) {
        LOGGER.info("Update department");
        if (Strings.isBlank(managerEmail)) {
            throw new IllegalArgumentException("Manager email must not be blank");
        }
        ApplicationUser manager = userRepository.findByEmail(managerEmail)
            .orElseThrow(() -> new NotFoundException("User with email " + managerEmail + " not found"));
        if (manager.getRole() != Role.MANAGER) {
            throw new IllegalArgumentException("User with email " + managerEmail + " is not a manager");
        }
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));
        List<ApplicationUser> users = userRepository.findAllByEmailIn(userEmails);
        if (userEmails != null && userEmails.size() != users.size()) {
            throw new NotFoundException("Not all users with emails " + String.join(", ", userEmails) + " found");
        }
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException("Department name must not be blank");
        }
        department.setName(name);
        department.setManager(manager);
        department.setMembers(users);
        department = departmentRepository.save(department);
        Hibernate.initialize(department.getProjects());
        var projectsDto = projectMapper.projectToDetailProjectDto(department.getProjects());
        DepartmentDto dto = new DepartmentDto(department.getId(), department.getName(),
            userMapper.applicationUserToDetailedUserDto(department.getManager()),
            department.getMembers().stream().map(userMapper::applicationUserToDetailedUserDto).collect(Collectors.toList()),
            projectsDto);
        return dto;
    }

    @Override
    public void deleteDepartment(Long id) {
        LOGGER.info("Delete department");
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));
        departmentRepository.delete(department);
    }

    @Transactional
    @Override
    public void addUserToDepartment(Long id, String email) {
        LOGGER.info("Add user to department");
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));
        ApplicationUser user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
        if (department.getMembers().contains(user)) {
            throw new IllegalArgumentException("User with email " + email + " is already in department");
        }
        if (user.getDepartment() != null) {
            throw new IllegalArgumentException("User with email " + email + " is already in department " + user.getDepartment().getName());
        }
        department.getMembers().add(user);
        departmentRepository.save(department);

    }

}
