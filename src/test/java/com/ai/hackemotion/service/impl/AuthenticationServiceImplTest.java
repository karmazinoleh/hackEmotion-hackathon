package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.dto.request.AuthenticationRequest;
import com.ai.hackemotion.dto.request.RegistrationRequest;
import com.ai.hackemotion.dto.response.AuthenticationResponse;
import com.ai.hackemotion.entity.Role;
import com.ai.hackemotion.entity.Token;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.enums.EmailTemplateName;
import com.ai.hackemotion.enums.TokenType;
import com.ai.hackemotion.repository.RoleRepository;
import com.ai.hackemotion.repository.TokenRepository;
import com.ai.hackemotion.repository.UserRepository;
import com.ai.hackemotion.security.service.impl.JwtServiceImpl;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private EmailServiceImpl emailServiceImpl;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtServiceImpl jwtServiceImpl;
    @Mock
    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationServiceImpl(
                roleRepository,
                passwordEncoder,
                userRepository,
                tokenRepository,
                emailServiceImpl,
                authenticationManager,
                jwtServiceImpl
        );
    }

    @Test
    void register_ShouldSaveNewUser_WhenUserDoesNotExist() throws MessagingException, InstanceAlreadyExistsException {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setFullName("fullName");
        request.setEmail("test@example.com");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        authenticationService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailServiceImpl, times(1)).sendEmail(
                anyString(),
                anyString(),
                eq(EmailTemplateName.ACTIVATE_ACCOUNT),
                nullable(String.class),
                anyString(),
                anyString()
        );
    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setFullName("fullName");
        request.setEmail("test@example.com");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(IllegalStateException.class, () -> authenticationService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_ShouldReturnAuthenticationResponse_WhenCredentialsAreValid() {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password");
        User user = User.builder()
                .email(request.getEmail())
                .password("password")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtServiceImpl.generateToken(anyMap(), eq(user))).thenReturn("accessToken");
        when(jwtServiceImpl.generateRefreshToken(user)).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void activateAccount_ShouldEnableUser_WhenTokenIsValid() throws MessagingException {
        User user = User.builder()
                .id(1L)
                .enabled(false)
                .build();
        Token token = Token.builder()
                .token("validToken")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();

        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(token));
        when(userRepository.findById(String.valueOf(user.getId()))).thenReturn(Optional.of(user));

        authenticationService.activateAccount("validToken");

        assertTrue(user.isEnabled());
        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void activateAccount_ShouldThrowException_WhenTokenExpired() {
        User user = User.builder()
                .id(1L)
                .build();
        Token token = Token.builder()
                .token("expiredToken")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .user(user)
                .build();

        when(tokenRepository.findByToken("expiredToken")).thenReturn(Optional.of(token));

        assertThrows(RuntimeException.class, () -> authenticationService.activateAccount("expiredToken"));
    }

    @Test
    void saveUserToken_ShouldSaveToken() {
        User user = new User();
        authenticationService.saveUserToken(user, "jwtToken", TokenType.ACCESS);

        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void revokeAllUserTokens_ShouldMarkTokensAsRevoked() {
        User user = new User();
        Token token = new Token();
        List<Token> tokens = Collections.singletonList(token);

        when(tokenRepository.findAllValidTokenByUser(any())).thenReturn(tokens);

        authenticationService.revokeAllUserTokens(user);

        assertTrue(token.isExpired());
        assertTrue(token.isRevoked());
        verify(tokenRepository, times(1)).saveAll(tokens);
    }

    @Test
    void revokeAllUserAccessTokens_ShouldOnlyRevokeAccessTokens() {
        User user = new User();
        Token accessToken = Token.builder().tokenType(TokenType.ACCESS).build();
        Token refreshToken = Token.builder().tokenType(TokenType.REFRESH).build();
        List<Token> tokens = List.of(accessToken, refreshToken);

        when(tokenRepository.findAllValidTokenByUser(any())).thenReturn(tokens);

        authenticationService.revokeAllUserAccessTokens(user);

        assertTrue(accessToken.isExpired());
        assertTrue(accessToken.isRevoked());
        assertFalse(refreshToken.isExpired());
        assertFalse(refreshToken.isRevoked());
        verify(tokenRepository, times(1)).saveAll(tokens);
    }
}
