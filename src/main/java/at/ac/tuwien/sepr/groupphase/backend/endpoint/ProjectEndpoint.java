package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailLazyProjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailProjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ProjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ProjectMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/projects")
public class ProjectEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ProjectService projectService;
    private ProjectMapper projectMapper;

    public ProjectEndpoint(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new project", security = @SecurityRequirement(name = "apiKey"))
    public DetailProjectDto createProject(@Valid @RequestBody ProjectCreateDto userRegisterDto) {
        LOGGER.info("POST /api/v1/projects/{}", userRegisterDto);
        return projectMapper.projectToDetailProjectDto(projectService.createProject(userRegisterDto));
    }


    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @PutMapping
    @Operation(summary = "Update a projects name or members", security = @SecurityRequirement(name = "apiKey"))
    public DetailProjectDto updateProject(@RequestBody DetailProjectDto detailProjectDto) {
        LOGGER.info("Put /api/v1/projects/{}", detailProjectDto);
        return projectMapper.projectToDetailProjectDto(projectService.updateProject(detailProjectDto));
    }

    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @GetMapping(value = "{projectId}")
    @Operation(summary = "Get a project by its id", security = @SecurityRequirement(name = "apiKey"))
    public DetailProjectDto getById(@PathVariable Long projectId) {
        LOGGER.info("Get /api/v1/projects/{}", projectId);
        return projectMapper.projectToDetailProjectDto(projectService.getById(projectId));
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping
    @Operation(summary = "Get a project by its id", security = @SecurityRequirement(name = "apiKey"))
    public List<DetailLazyProjectDto> getAll() {
        LOGGER.info("Get /api/v1/projects");
        return projectMapper.projectToDetailLazyProjectDto(projectService.getAll());
    }

    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @GetMapping("ofDepartment/{managerEmail}")
    @Operation(summary = "Get a project by its id", security = @SecurityRequirement(name = "apiKey"))
    public List<DetailLazyProjectDto> getAllOfDepartment(@PathVariable String managerEmail) {
        LOGGER.info("Get /api/v1/projects/ofDepartment/{}", managerEmail);
        return projectMapper.projectToDetailLazyProjectDto(projectService.getAllOfDepartment(managerEmail));
    }

    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @DeleteMapping("{id}")
    @Operation(summary = "Delete a project by its id", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        LOGGER.info("Delete /api/v1/projects/" + id);
        projectService.deleteProject(id);
    }

}
