package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ProjectCreateDto {

    @NotNull(message = " must not be null")
    @Size(min = 1, max = 100, message = " must be between 1 and 100 characters")
    @NotBlank(message = " must not be blank")
    private String name;
    @NotNull(message = " must not be null")
    @Email
    @Size(min = 1, max = 100, message = " must be between 1 and 100 characters")
    @NotBlank(message = " must not be blank")
    private String managerEmail;

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static class ProjectCreateDtoBuilder {

        private String name;

        private String managerEmail;


        public static ProjectCreateDto.ProjectCreateDtoBuilder aDefaultProject() {
            return new ProjectCreateDto.ProjectCreateDtoBuilder();
        }

        public ProjectCreateDto.ProjectCreateDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProjectCreateDto.ProjectCreateDtoBuilder withManagerEmail(String managerEmail) {
            this.managerEmail = managerEmail;
            return this;
        }

        public ProjectCreateDto build() {
            ProjectCreateDto project = new ProjectCreateDto();
            project.setName(this.name);
            project.setManagerEmail(this.managerEmail);
            return project;
        }
    }
}
