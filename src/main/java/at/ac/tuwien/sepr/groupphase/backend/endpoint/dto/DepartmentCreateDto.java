package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DepartmentCreateDto(
    @NotBlank(message = "Department name must not be blank")
    @NotNull(message = "Department name must not be null")
    @Size(min = 1, max = 100, message = "Department name must have between 1 and 100 chars")
    String name,

    @Email
    @NotNull(message = "Manager email must not be null")
    String manager,
    List<String> members
) {
}
