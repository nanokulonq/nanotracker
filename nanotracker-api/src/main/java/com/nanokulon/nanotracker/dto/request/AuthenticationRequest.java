package com.nanokulon.nanotracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @NotBlank(message = "{users.auth.errors.username_is_null}")
    private String username;
    @NotBlank(message = "{users.auth.errors.password_is_null}")
    private String password;
}
