package com.ai.hackemotion.auth;

import com.ai.hackemotion.email.EmailService;
import com.ai.hackemotion.email.EmailTemplateName;
import com.ai.hackemotion.role.Role;
import com.ai.hackemotion.role.RoleRepository;
import com.ai.hackemotion.security.JwtService;
import com.ai.hackemotion.user.Token;
import com.ai.hackemotion.user.TokenRepository;
import com.ai.hackemotion.user.User;
import com.ai.hackemotion.user.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthenticationService authenticationService;


    private Role userRole;
    private User user;
    private RegistrationRequest registrationRequest;
    private AuthenticationRequest authenticationRequest;
    private final String ENCODED_PASSWORD = "encodedPassword";
    private final String AUTH_TOKEN = "autenticationToken";

    @BeforeEach
    void setUp() {
        this.user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword(ENCODED_PASSWORD);
        user.setEmail("email@email.com");
        user.setEnabled(false);

        userRole = Role.builder()
                .id(1L)
                .name("USER")
                .build();

        registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("testUser");
        registrationRequest.setPassword("password");
        registrationRequest.setEmail("test@email.com");
        registrationRequest.setFullName("Test User");

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@email.com");
        authenticationRequest.setPassword("password");

        ReflectionTestUtils.setField(authenticationService, "activationUrl", "http://localhost:3000/activate");
    }

    @Test
    void register_Success() throws MessagingException, InstanceAlreadyExistsException {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

        authenticationService.register(registrationRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals(registrationRequest.getUsername(), capturedUser.getUsername());
        assertEquals(ENCODED_PASSWORD, capturedUser.getPassword());
        assertEquals(registrationRequest.getEmail(), capturedUser.getEmail());
        assertEquals(registrationRequest.getFullName(), capturedUser.getFullName());
        assertFalse(capturedUser.isEnabled());
        assertFalse(capturedUser.isAccountLocked());
        assertEquals(0, capturedUser.getDatasetsUploaded());
        assertEquals(0, capturedUser.getDatasetsRated());

        assertEquals(1, capturedUser.getRoleList().size());
        assertEquals(userRole, capturedUser.getRoleList().get(0));

        verify(tokenRepository).save(any());
        verify(emailService).sendEmail(
                eq(registrationRequest.getEmail()),
                eq(registrationRequest.getUsername()),
                eq(EmailTemplateName.ACTIVATE_ACCOUNT),
                eq("http://localhost:3000/activate"),
                anyString(),
                eq("Account activation")
        );
    }

    @Test
    void register_RoleNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
                authenticationService.register(registrationRequest);
        });

        assertEquals("Role user was not init.!", exception.getMessage());

        verify(roleRepository).findByName("USER");
        verify(userRepository).findByUsername("testUser");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_UsernameAlreadyInUse(){
        User existingUser = User.builder()
                        .id(1L)
                        .username("testUser")
                        .build();

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(existingUser));

        Exception exeption = assertThrows(IllegalStateException.class, () -> {
            authenticationService.register(registrationRequest);
        });

        assertEquals("Username already in use: testUser", exeption.getMessage());

        verify(userRepository).findByUsername("testUser");
        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_EmailServiceThrowsException() throws MessagingException {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

        doThrow(new MessagingException("Email sending failed"))
                .when(emailService).sendEmail(
                        anyString(), anyString(), any(), anyString(), anyString(), anyString()
                );

        MessagingException exception = assertThrows(MessagingException.class, () -> {
            authenticationService.register(registrationRequest);
        });

        assertEquals("Email sending failed", exception.getMessage());

        verify(userRepository).save(any(User.class));
        verify(roleRepository).findByName("USER");
    }

    @Test
    void authenticate_Success(){

        // Arrange:
        var userDetails = User.builder().email("test@email.com").password("password").build();
        var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, "password");

        when(authenticationManager.authenticate(any())).thenReturn(authenticationToken);

        var claims = new HashMap<String, Object>();
        claims.put("username", userDetails.getUsername());
        when(jwtService.generateToken(claims, userDetails)).thenReturn(AUTH_TOKEN);

        // Act
        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(AUTH_TOKEN, response.getToken());
    }

    @Test
    void authenticate_BadCredentials(){
        // Arrange
        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager).authenticate(any());

        // Act
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(authenticationRequest);
        });

        // Assert
        assertEquals("Bad credentials", exception.getMessage());
    }

    @Test
    void activateAccount_Success() throws MessagingException {
        // Arrange
        var token = Token.builder()
                        .token("activateAccountToken")
                        .createdAt(LocalDateTime.now().minusMinutes(5))
                        .expiresAt(LocalDateTime.now().plusMinutes(5))
                        .user(user)
                        .build();

        when(tokenRepository.findByToken("activateAccountToken")).thenReturn(Optional.ofNullable(token));
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        // Act
        authenticationService.activateAccount("activateAccountToken");

        // Assert
        assertTrue(user.isEnabled());
        assertNotNull(token.getValidatedAt());

        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    void activateAccount_ExpiredToken() throws RuntimeException {
        // Arrange
        var token = Token.builder()
                .token("activateAccountToken")
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .user(user)
                .build();

        when(tokenRepository.findByToken("activateAccountToken")).thenReturn(Optional.ofNullable(token));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.activateAccount("activateAccountToken");
        });

        // Assert
        assertEquals("Activation token has expired. A new token has been send to the same email address", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void activateAccount_InvalidToken() throws RuntimeException {
        // Arrange
        when(tokenRepository.findByToken("activateAccountToken")).thenReturn(Optional.empty());

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.activateAccount("activateAccountToken");
        });

        // Assert
        verify(userRepository, never()).save(any(User.class));
        verify(tokenRepository, never()).save(any(Token.class));
    }
}