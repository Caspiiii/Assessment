package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UserDeleteDto {


    @NotNull(message = "Username must not be null")
    @Size(min = 1, max = 100, message = "Username must be between 1 and 100 characters")
    @NotBlank(message = "Username must not be blank")
    @Email(message = "Email is not valid")
    private String username;



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDeleteDto userDeleteDto)) {
            return false;
        }
        return username.equals(userDeleteDto.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "UserRegisterDto{"
            + "username='" + username + '\''

            + '}';
    }

    public static final class UserDeleteDtoBuilder {
        private String username;
        private String password;

        private UserDeleteDtoBuilder() {
        }

        public static UserDeleteDtoBuilder anUserRegisterDto() {
            return new UserDeleteDtoBuilder();
        }

        public UserDeleteDtoBuilder withEmail(String email) {
            this.username = email;
            return this;
        }

        public UserDeleteDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserDeleteDto build() {
            UserDeleteDto userDeleteDto = new UserDeleteDto();
            userDeleteDto.setUsername(username);
            return userDeleteDto;
        }
    }
}

