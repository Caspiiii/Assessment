package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.util.List;

public class DetailProjectDto {

    @NotNull
    private Long id;

    @Size(min = 1, max = 100, message = " must be between 1 and 100 characters")
    @NotBlank(message = " must not be blank")
    @Email
    @NotNull
    private String name;

    @NotNull
    private List<SimpleUserDto> members;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SimpleUserDto> getMembers() {
        return members;
    }

    public void setMembers(List<SimpleUserDto> members) {
        this.members = members;
    }


    public static class DetailProjectDtoBuilder {

        private Long id;

        private String name;

        private List<SimpleUserDto> members;


        public static DetailProjectDto.DetailProjectDtoBuilder aDefaultProject() {
            return new DetailProjectDto.DetailProjectDtoBuilder();
        }

        public DetailProjectDto.DetailProjectDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DetailProjectDto.DetailProjectDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DetailProjectDto.DetailProjectDtoBuilder withMembers(List<SimpleUserDto> members) {
            this.members = members;
            return this;
        }


        public DetailProjectDto build() {
            DetailProjectDto project = new DetailProjectDto();
            project.setId(this.id);
            project.setName(this.name);
            project.setMembers(this.members);
            return project;
        }
    }
}
