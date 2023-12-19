package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class DetailUserDto {

    private Long id;

    @NotNull(message = "Email must not be null")
    @Email
    @Size(min = 1, max = 100, message = "Email must be between 1 and 100 characters")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotNull(message = "Password must not be null")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotNull(message = "First name must not be null")
    @Size(min = 1, max = 100, message = "Firstname must be between 1 and 100 characters")
    @NotBlank(message = "Firstname must not be blank")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    @Size(min = 1, max = 100, message = "Lastname must be between 1 and 100 characters")
    @NotBlank(message = "Lastname must not be blank")
    private String lastName;

    private Role role;

    private DepartmentDto department;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetailUserDto that)) {
            return false;
        }

        if (!id.equals(that.id)) {
            return false;
        }
        if (!email.equals(that.email)) {
            return false;
        }
        if (!password.equals(that.password)) {
            return false;
        }
        if (!firstName.equals(that.firstName)) {
            return false;
        }
        if (!lastName.equals(that.lastName)) {
            return false;
        }
        if (role != that.role) {
            return false;
        }
        return Objects.equals(department, that.department);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + (department != null ? department.hashCode() : 0);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public DepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDto department) {
        this.department = department;
    }

    public static class DetailUserDtoBuilder {
        private Long id;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private Role role;

        private DepartmentDto department;

        private DetailUserDtoBuilder() {
        }

        public static DetailUserDtoBuilder aDetailUserDto() {
            return new DetailUserDtoBuilder();
        }

        public DetailUserDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DetailUserDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public DetailUserDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public DetailUserDtoBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public DetailUserDtoBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public DetailUserDtoBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        public DetailUserDtoBuilder withDepartment(DepartmentDto department) {
            this.department = department;
            return this;
        }


        public DetailUserDto build() {
            DetailUserDto detailUserDto = new DetailUserDto();
            detailUserDto.setId(id);
            detailUserDto.setEmail(email);
            detailUserDto.setPassword(password);
            detailUserDto.setFirstName(firstName);
            detailUserDto.setLastName(lastName);
            detailUserDto.setRole(role);
            detailUserDto.setDepartment(department);
            return detailUserDto;
        }
    }
}
