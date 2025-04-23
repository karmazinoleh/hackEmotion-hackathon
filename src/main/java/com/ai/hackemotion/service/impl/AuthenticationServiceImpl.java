package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.dto.AuthenticationRequest;
import com.ai.hackemotion.dto.AuthenticationResponse;
import com.ai.hackemotion.dto.RegistrationRequest;
import com.ai.hackemotion.enums.EmailTemplateName;
import com.ai.hackemotion.repository.RoleRepository;
import com.ai.hackemotion.entity.Token;
import com.ai.hackemotion.repository.TokenRepository;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.repository.UserRepository;
import com.ai.hackemotion.service.AuthenticationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl  implements AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtServiceImpl;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    @Autowired
    private RatingServiceImpl rating;


    public void register(RegistrationRequest request) throws MessagingException, InstanceAlreadyExistsException {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already in use: " + request.getUsername());
        } else {

            var userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new IllegalStateException("Role user was not init.!"));

            var user = User.builder().username(request.getUsername())
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .accountLocked(false)
                    .enabled(false)
                    .datasetsUploaded(0)
                    .datasetsRated(0)
                    .roleList(List.of(userRole))
                    .build();
            userRepository.save(user);
            sendValidationEmail(user);
        }
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailServiceImpl.sendEmail(
                user.getEmail(),
                user.getUsername(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }
    private String generateAndSaveActivationToken(User user){
        String generatedToken = generateActivationCode(6);
        var token = Token.builder().token(generatedToken)
                .createdAt(LocalDateTime.now()).expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user).build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int lenght) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i = 0; i < lenght; i++){
            int randomIndex = secureRandom.nextInt(characters.length()); // 0 -> 9
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );
        var claims = new HashMap<String, Object>();
        var user = ((UserDetails)auth.getPrincipal());
        //var user = userRepository.findByEmail(request.getEmail())
        //        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        claims.put("username", user.getUsername());
        var jwtToken = jwtServiceImpl.generateToken(claims, user);
        //rating.updateScore((User) auth.getPrincipal(), 5);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepository.findById(String.valueOf(savedToken.getUser().getId()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
