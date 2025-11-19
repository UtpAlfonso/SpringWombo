package com.jugueteria.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordForgotRequest {
    @NotBlank
    @Email
    private String email;
}