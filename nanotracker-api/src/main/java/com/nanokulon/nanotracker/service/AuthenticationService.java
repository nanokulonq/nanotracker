package com.nanokulon.nanotracker.service;

import com.nanokulon.nanotracker.dto.request.AuthenticationRequest;
import com.nanokulon.nanotracker.dto.response.AuthenticationResponse;
import com.nanokulon.nanotracker.dto.request.RegistrationRequest;

public interface AuthenticationService {

    void register(RegistrationRequest registrationRequest);

    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
