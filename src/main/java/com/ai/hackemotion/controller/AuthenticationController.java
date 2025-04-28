package com.ai.hackemotion.controller;

import com.ai.hackemotion.dto.request.AuthenticationRequest;
import com.ai.hackemotion.dto.response.AuthenticationResponse;
import com.ai.hackemotion.dto.response.RegistrationResponce;
import com.ai.hackemotion.enums.TokenType;
import com.ai.hackemotion.repository.UserRepository;
import com.ai.hackemotion.security.service.impl.JwtServiceImpl;
import com.ai.hackemotion.service.impl.AuthenticationServiceImpl;
import com.ai.hackemotion.dto.request.RegistrationRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceAlreadyExistsException;
import java.io.IOException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationServiceImpl;
    private final UserRepository userRepository;
    private final JwtServiceImpl jwtServiceImpl;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RegistrationResponce> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException, InstanceAlreadyExistsException {
        authenticationServiceImpl.register(request);
        RegistrationResponce response = RegistrationResponce.builder()
                .message("Registration successful. Please check your email to activate your account.")
                .success(true)
                .build();
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ){
        return ResponseEntity.ok(authenticationServiceImpl.authenticate(request));
    }

    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        authenticationServiceImpl.activateAccount(token);
    }

    @PostMapping("/refresh-token")
    public AuthenticationResponse refreshToken(
            HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token");
        }
        refreshToken = authHeader.substring(7);
        username = jwtServiceImpl.extractUsername(refreshToken);

        if (username != null) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow();
            if (jwtServiceImpl.isTokenValid(refreshToken, user)) {
                var accessToken = jwtServiceImpl.generateToken(user);

                // відкликаємо ВСІ access токени користувача
                authenticationServiceImpl.revokeAllUserAccessTokens(user);

                // зберігаємо новий access токен
                authenticationServiceImpl.saveUserToken(user, accessToken, TokenType.ACCESS);

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    }

}
