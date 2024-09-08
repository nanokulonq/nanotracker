package com.nanokulon.nanotracker.service;

import com.nanokulon.nanotracker.dto.request.AuthenticationRequest;
import com.nanokulon.nanotracker.dto.response.AuthenticationResponse;
import com.nanokulon.nanotracker.dto.request.RegistrationRequest;
import com.nanokulon.nanotracker.entity.TrackerUser;
import com.nanokulon.nanotracker.exception.UserAlreadyExistsException;
import com.nanokulon.nanotracker.repository.TrackerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {

    private final TrackerUserRepository trackerUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public void register(RegistrationRequest registrationRequest) {
        if (this.isExistsByUsername(registrationRequest.getUsername())) {
            throw new UserAlreadyExistsException("users.register.errors.username_already_exists");
        }
        if (this.isExistsByEmail(registrationRequest.getEmail())) {
            throw new UserAlreadyExistsException("users.register.errors.email_already_exists");
        }
        this.trackerUserRepository.save(TrackerUser.builder()
                .email(registrationRequest.getEmail())
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        UserDetails userDetails = userDetailsService.
                loadUserByUsername(authenticationRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }

    private boolean isExistsByUsername(String username) {
        return this.trackerUserRepository.existsByUsername(username);
    }

    private boolean isExistsByEmail(String email) {
        return this.trackerUserRepository.existsByEmail(email);
    }

}
