package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UserRegisterDto {

    @NotNull(message = "Firstname must not be null")
    @Size(min = 1, max = 100, message = "Firstname must be between 1 and 100 characters")
    @NotBlank(message = "Firstname must not be blank")
    private String firstName;

    @NotNull(message = "Lastname must not be null")
    @Size(min = 1, max = 100, message = "Lastname must be between 1 and 100 characters")
    @NotBlank(message = "Lastname must not be blank")
    private String lastName;
    @NotNull(message = "Email must not be null")
    @Size(min = 1, max = 100, message = "Email must be between 1 and 100 characters")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email is not valid")
    private String username;
    @NotNull(message = "Password must not be null")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @NotBlank(message = "Password must not be blank")
    private String password;
    @NotNull(message = "Mode must not be null")
    @Size(min = 1, max = 100, message = "Mode must be between 1 and 100 characters")
    @NotBlank(message = "Mode must not be blank")
    private String mode;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserRegisterDto userRegisterDto)) {
            return false;
        }
        return firstName.equals(userRegisterDto.firstName)
            && lastName.equals(userRegisterDto.lastName)
            && username.equals(userRegisterDto.username)
            && password.equals(userRegisterDto.password)
            && mode.equals(userRegisterDto.mode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, username, password, mode);
    }

    @Override
    public String toString() {
        return "UserRegisterDto{"
            + "firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", username='" + username + '\''
            + ", password='" + password + '\''
            + ", mode='" + mode + '\''
            + '}';
    }

    public static final class UserRegisterDtoBuilder {
        private String firstName;
        private String lastName;
        private String username;
        private String password;
        private String mode;

        private UserRegisterDtoBuilder() {
        }

        public static UserRegisterDtoBuilder anUserRegisterDto() {
            return new UserRegisterDtoBuilder();
        }

        public UserRegisterDtoBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserRegisterDtoBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserRegisterDtoBuilder withEmail(String email) {
            this.username = email;
            return this;
        }

        public UserRegisterDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserRegisterDtoBuilder withRole(String mode) {
            this.mode = mode;
            return this;
        }

        public UserRegisterDto build() {
            UserRegisterDto userRegisterDto = new UserRegisterDto();
            userRegisterDto.setFirstName(firstName);
            userRegisterDto.setLastName(lastName);
            userRegisterDto.setUsername(username);
            userRegisterDto.setPassword(password);
            userRegisterDto.setMode(mode);
            return userRegisterDto;
        }
    }
}

