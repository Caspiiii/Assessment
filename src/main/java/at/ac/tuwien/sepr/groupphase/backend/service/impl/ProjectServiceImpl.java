package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.DepartmentEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailProjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ProjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ProjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepositoryInterface;
import at.ac.tuwien.sepr.groupphase.backend.service.ProjectService;
import org.aspectj.weaver.ast.Not;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class ProjectServiceImpl implements ProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ProjectRepository projectRepository;
    private UserRepositoryInterface userRepositoryInterface;
    private DepartmentRepository departmentRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepositoryInterface userRepositoryInterface, DepartmentRepository departmentRepository) {
        this.projectRepository = projectRepository;
        this.userRepositoryInterface = userRepositoryInterface;
        this.departmentRepository = departmentRepository;
    }


    @Override
    @Transactional
    public Project createProject(ProjectCreateDto projectCreateDto) throws NotFoundException {
        LOGGER.trace("create Project " + projectCreateDto);
        Project project = new Project();
        project.setName(projectCreateDto.getName());
        ArrayList<ApplicationUser> list = new ArrayList<>();
        project.setMembers(list);

        Department department = departmentRepository.findByManagerEmail(projectCreateDto.getManagerEmail()).orElse(null);
        project.setDepartment(department);

        if (department != null) {
            Hibernate.initialize(department.getProjects());
        } else {
            throw new NotFoundException("Department could not be found");
        }

        var newProject = this.projectRepository.save(project);
        department.getProjects().add(project);
        this.departmentRepository.save(department);

        return newProject;
    }

    @Override
    @Transactional
    public Project getById(Long projectId) throws NotFoundException {
        LOGGER.trace("get Project with id " + projectId);
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project != null) {
            Hibernate.initialize(project.getMembers());
            Hibernate.initialize(project.getDepartment());
        } else {
            throw new NotFoundException("Project with given id cannot be found");
        }
        return project;
    }

    @Override
    public List<Project> getAll() {
        LOGGER.trace("get all projects");
        return projectRepository.findAll();
    }

    @Override
    public List<Project> getAllOfDepartment(String managerEmail) throws NotFoundException {
        LOGGER.trace("get all projects of a department");
        Department department = departmentRepository.findByManagerEmail(managerEmail).orElse(null);
        if (department == null) {
            throw new NotFoundException("Department was not found");
        }
        var projects = projectRepository.findByDepartment(department);
        return projects;
    }


    @Override
    public Project updateProject(DetailProjectDto project) throws NotFoundException {
        LOGGER.trace("update project {} ", project);
        Project oldProject = projectRepository.findById(project.getId()).orElse(null);
        if (oldProject == null) {
            throw new NotFoundException("Project to update not found");
        }
        List<ApplicationUser> userList = new ArrayList<>();
        for (SimpleUserDto user : project.getMembers()) {
            ApplicationUser userEntity = userRepositoryInterface.findByEmail(user.email()).orElse(null);
            if (userEntity == null) {
                throw new NotFoundException("User to add cannot be found");
            }
            if (userEntity.getDepartment() == null || !Objects.equals(userEntity.getDepartment().getId(), oldProject.getDepartment().getId())) {
                throw new NotFoundException("User to add must be a member of the department");
            }
            userList.add(userEntity);
        }
        Project projectEntity = Project.ProjectBuilder.aDefaultProject().withId(project.getId())
            .withName(project.getName())
            .withDepartment(oldProject.getDepartment())
            .withMembers(userList)
            .build();
        return projectRepository.save(projectEntity);
    }

    @Override
    public void deleteProject(Long id) {
        LOGGER.trace("delete project: " + id);
        projectRepository.deleteById(id);
    }
}
