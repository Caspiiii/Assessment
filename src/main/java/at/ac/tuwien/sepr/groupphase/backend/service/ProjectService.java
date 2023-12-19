package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailProjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ProjectCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Project;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

import java.util.List;

/**
 * Service Interface for accessing and altering data of projects.
 */
public interface ProjectService {

    /**
     * creates a new project with a given name and an empty members list.
     *
     * @param projectCreateDto the to be created project with its name
     * @return the newly created project
     * @throws NotFoundException if the specified department cannot be found
     */
    public Project createProject(ProjectCreateDto projectCreateDto) throws NotFoundException;

    /**
     * Updates a project in the persistent data store.
     *
     * @param project the project to update
     * @return the updated project
     * @throws NotFoundException if the to be updated project cannot be found
     */
    public Project updateProject(DetailProjectDto project) throws NotFoundException;

    /**
     * gets information of the project with the given id.
     *
     * @param projectId id of the project to get
     * @return the project with the given id
     * @throws NotFoundException if the project with the given id cannot be found
     */
    public Project getById(Long projectId) throws NotFoundException;

    /**
     * gets all projects.
     *
     * @return a list of all projects
     */
    public List<Project> getAll();

    /**
     * gets all projects of a department.
     *
     * @param managerEmail the email of the manager of the department
     * @return a list of all projects within the specified department
     * @throws NotFoundException if the specified department cannot be found
     */
    List<Project> getAllOfDepartment(String managerEmail) throws NotFoundException;

    /**
     * Deletes the project with the specified id.
     *
     * @param id the id of the project to be deleted
     */
    public void deleteProject(Long id);


}
