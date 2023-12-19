package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DepartmentCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepr.groupphase.backend.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/department")
public class DepartmentEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentEndpoint(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @PostMapping
    public DepartmentDto create(@Valid @RequestBody DepartmentCreateDto dto) {
        LOGGER.info("Put /api/v1/department body: {}", dto);
        try {
            return departmentService.createDepartment(dto.name(), dto.manager(), dto.members());
        } catch (DataIntegrityViolationException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping()
    public List<DepartmentDto> getAll() {
        LOGGER.info("Get /api/v1/departments");
        return departmentService.getAllDepartments();
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @GetMapping(value = "{id}")
    public DepartmentDto getById(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/department/" + id);
        return departmentService.getDepartmentById(id);
    }

    @Secured("ROLE_MANAGER")
    @GetMapping(value = "manager/{email}")
    public DepartmentDto getByManagerEmail(@PathVariable String email) {
        LOGGER.info("Get Department bei manager email:" + email);
        LOGGER.info("GET /api/v1/department/manager/" + email);
        return departmentService.getDepartmentByManagerEmail(email);
    }

    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @PutMapping(value = "{id}")
    public DepartmentDto updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentDto dto) {
        LOGGER.info("PUT /api/v1/department/" + id + " body: {}", dto);
        try {
            return departmentService.updateDepartment(id, dto.name(), dto.manager().email(),
                dto.members().stream().map(SimpleUserDto::email).toList());
        } catch (DataIntegrityViolationException | IllegalArgumentException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }
}
