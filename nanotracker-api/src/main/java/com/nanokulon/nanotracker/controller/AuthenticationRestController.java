package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.dto.request.AuthenticationRequest;
import com.nanokulon.nanotracker.dto.request.RegistrationRequest;
import com.nanokulon.nanotracker.dto.response.AuthenticationResponse;
import com.nanokulon.nanotracker.exception.UserAlreadyExistsException;
import com.nanokulon.nanotracker.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;

    @PostMapping("/register")
    public ResponseEntity<ProblemDetail> register(@Valid @RequestBody RegistrationRequest registrationRequest,
                                                  BindingResult bindingResult, Locale locale) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            try {
                this.authenticationService.register(registrationRequest);
                return ResponseEntity
                        .noContent()
                        .build();
            } catch (UserAlreadyExistsException exception) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                                this.messageSource.getMessage(exception.getMessage(), new Object[0],
                                        exception.getMessage(), locale)));
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest, Locale locale) {
        try {
            AuthenticationResponse authenticationResponse = this.authenticationService
                    .authenticate(authenticationRequest);
            return ResponseEntity.ok(authenticationResponse);
        } catch (BadCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                            this.messageSource.getMessage(exception.getMessage(), new Object[0],
                                    exception.getMessage(), locale)));
        }
    }
}
