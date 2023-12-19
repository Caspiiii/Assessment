package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {

    /**
     * Creates a new department.
     *
     * @param name the name of the department
     * @param managerEmail the email of the manager of the department
     * @param userEmails the emails of the members of the department
     */
    DepartmentDto createDepartment(String name, String managerEmail, List<String> userEmails);

    /**
     * Returns all departments.
     *
     * @return all departments
     */
    List<DepartmentDto> getAllDepartments();

    /**
     * Returns the department with the given id.
     *
     * @param id the id of the department
     * @return the department with the given id
     */
    DepartmentDto getDepartmentById(Long id);

    /**
     * Returns the department of the manager with the given email.
     *
     * @param email the email of the manager
     * @return the department of the manager with the given email
     */
    DepartmentDto getDepartmentByManagerEmail(String email);

    /**
     * Updates the department with the given id.
     *
     * @param id the id of the department
     * @param name the name of the department
     * @param managerEmail the email of the manager of the department
     * @param userEmails the emails of the members of the department
     * @return the updated department
     */
    DepartmentDto updateDepartment(Long id, String name, String managerEmail, List<String> userEmails);

    /**
     * Deletes the department with the given id.
     *
     * @param id the id of the department
     */
    void deleteDepartment(Long id);

    /**
     * Adds the user with the given email to the department with the given id.
     *
     * @param id the id of the department
     * @param email the email of the user
     */
    void addUserToDepartment(Long id, String email);


}
