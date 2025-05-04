package com.ai.hackemotion.controller;

import com.ai.hackemotion.dto.request.AuthenticationRequest;
import com.ai.hackemotion.dto.request.RegistrationRequest;
import com.ai.hackemotion.dto.response.AuthenticationResponse;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.enums.TokenType;
import com.ai.hackemotion.repository.UserRepository;
import com.ai.hackemotion.security.service.impl.JwtServiceImpl;
import com.ai.hackemotion.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import javax.management.InstanceAlreadyExistsException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtServiceImpl jwtServiceImpl;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldReturnAcceptedStatus() throws MessagingException, InstanceAlreadyExistsException {
        RegistrationRequest request = new RegistrationRequest();

        ResponseEntity<?> response = authenticationController.register(request);

        verify(authenticationServiceImpl, times(1)).register(request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void authenticate_shouldReturnAuthenticationResponse() {
        AuthenticationRequest request = new AuthenticationRequest();
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        when(authenticationServiceImpl.authenticate(request)).thenReturn(expectedResponse);

        ResponseEntity<AuthenticationResponse> response = authenticationController.authenticate(request);

        verify(authenticationServiceImpl, times(1)).authenticate(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void confirm_shouldActivateAccount() throws MessagingException {
        String token = "some-token";

        authenticationController.confirm(token);

        verify(authenticationServiceImpl, times(1)).activateAccount(token);
    }

    @Test
    void refreshToken_shouldReturnAuthenticationResponse() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        String refreshToken = "Bearer refresh-token";
        String username = "testUser";

        when(mockRequest.getHeader("Authorization")).thenReturn(refreshToken);
        when(jwtServiceImpl.extractUsername("refresh-token")).thenReturn(username);

        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtServiceImpl.isTokenValid("refresh-token", user)).thenReturn(true);
        when(jwtServiceImpl.generateToken(user)).thenReturn("new-access-token");

        AuthenticationResponse response = authenticationController.refreshToken(mockRequest);

        verify(authenticationServiceImpl, times(1)).revokeAllUserAccessTokens(user);
        verify(authenticationServiceImpl, times(1)).saveUserToken(user, "new-access-token", TokenType.ACCESS);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void refreshToken_shouldThrowExceptionWhenAuthorizationHeaderMissing() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationController.refreshToken(mockRequest);
        });

        assertEquals("401 UNAUTHORIZED \"Missing refresh token\"", exception.getMessage());
    }

    @Test
    void refreshToken_shouldThrowExceptionWhenTokenIsInvalid() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        String refreshToken = "Bearer refresh-token";
        String username = "testUser";

        when(mockRequest.getHeader("Authorization")).thenReturn(refreshToken);
        when(jwtServiceImpl.extractUsername("refresh-token")).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationController.refreshToken(mockRequest);
        });

        assertEquals("No value present", exception.getMessage());
    }
}
