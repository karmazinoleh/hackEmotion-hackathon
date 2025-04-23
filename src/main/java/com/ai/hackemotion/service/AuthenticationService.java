package com.ai.hackemotion.service;

import com.ai.hackemotion.dto.request.AuthenticationRequest;
import com.ai.hackemotion.dto.response.AuthenticationResponse;
import com.ai.hackemotion.dto.request.RegistrationRequest;
import jakarta.mail.MessagingException;

import javax.management.InstanceAlreadyExistsException;

public interface AuthenticationService {
    void register(RegistrationRequest request) throws MessagingException, InstanceAlreadyExistsException;
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void activateAccount(String token) throws MessagingException;
}
