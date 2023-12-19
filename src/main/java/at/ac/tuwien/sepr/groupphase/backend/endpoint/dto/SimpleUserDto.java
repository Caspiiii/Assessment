package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SimpleUserDto(
    @NotNull(message = "First name must not be null")
    @Size(min = 1, max = 100, message = "Firstname must be between 1 and 100 characters")
    @NotBlank(message = "Firstname must not be blank")
    String firstName,

    @NotNull(message = "Last name must not be null")
    @Size(min = 1, max = 100, message = "Lastname must be between 1 and 100 characters")
    @NotBlank(message = "Lastname must not be blank")
    String lastName,
    @NotNull(message = "Email must not be null")
    @Email
    @Size(min = 1, max = 100, message = "Email must be between 1 and 100 characters")
    @NotBlank(message = "Email must not be blank")
    String email,
    @NotNull(message = "Password must not be null")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @NotBlank(message = "Password must not be blank")
    String password,

    @NotNull(message = "Role must be set")
    String role,

    String departmentName) {

}
