package com.nanokulon.nanotracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationRequest {
    @NotBlank
    @Email(message = "{users.register.errors.email_is_invalid}")
    private String email;
    @NotBlank
    private String username;
    @Size(min = 8, message = "{users.register.errors.password_size_is_invalid}")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
            message = "{users.register.errors.password_is_invalid}")
    private String password;
}
