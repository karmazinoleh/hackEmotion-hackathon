package com.ai.hackemotion.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotEmpty(message = "Email is empty!")
    @NotBlank(message = "Email is empty!")
    @Email(message = "Wrong format email")
    private String email;
    @NotEmpty(message = "Password is empty!")
    @NotBlank(message = "Password is empty!")
    @Size(min = 8, message = "Min. password length is 8 char.")
    private String password;
}
