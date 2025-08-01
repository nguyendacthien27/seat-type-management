package com.thiennd.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    @NotBlank(message = "User name is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
}
