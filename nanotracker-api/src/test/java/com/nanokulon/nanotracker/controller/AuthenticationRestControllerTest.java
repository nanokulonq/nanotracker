package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.dto.request.AuthenticationRequest;
import com.nanokulon.nanotracker.dto.request.RegistrationRequest;
import com.nanokulon.nanotracker.dto.response.AuthenticationResponse;
import com.nanokulon.nanotracker.exception.UserAlreadyExistsException;
import com.nanokulon.nanotracker.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты AuthenticationRestController")
class AuthenticationRestControllerTest {

    @Mock
    AuthenticationService authenticationService;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    AuthenticationRestController controller;

    @Test
    @DisplayName("register вернёт NoContent")
    void register_RequestIsValid_ReturnsNoContent() throws BindException {
        // given
        var request = new RegistrationRequest("email@gmail.com", "username", "password");
        var bindingResult = new MapBindingResult(Map.of(), "request");
        var locale = Locale.of("ru");

        // when
        var result = this.controller.register(request,
                bindingResult, locale);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.authenticationService).register(request);
        verifyNoMoreInteractions(this.authenticationService);
    }

    @Test
    @DisplayName("register преобразует исключение к BindException и выбросит его")
    void register_RequestIsInvalid_ThrowsBindException() {
        // given
        var request = new RegistrationRequest("email@gmail.com", "", "password");
        var bindingResult = new MapBindingResult(Map.of(), "request");
        bindingResult.addError(new FieldError("request", "username", "error"));
        var locale = Locale.of("ru");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.register(request,
                        bindingResult, locale));

        // then
        assertEquals(List.of(new FieldError("request", "username",
                "error")), exception.getAllErrors());
        verifyNoInteractions(this.authenticationService);
    }

    @Test
    @DisplayName("register выбросит BindException")
    void register_RequestIsInvalidAndBindResultIsBindException_ThrowsBindException() {
        // given
        var request = new RegistrationRequest("email@gmail.com", "", "password");
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "request"));
        bindingResult.addError(new FieldError("request", "username", "error"));
        var locale = Locale.of("ru");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.register(request,
                        bindingResult, locale));

        // then
        assertEquals(List.of(new FieldError("request", "username",
                "error")), exception.getAllErrors());
        verifyNoInteractions(this.authenticationService);
    }

    @Test
    @DisplayName("register вернёт Conflict")
    void register_UserAlreadyExists_ReturnsConflict() throws BindException {
        // given
        var request = new RegistrationRequest("email@gmail.com", "username", "password");
        var bindingResult = new MapBindingResult(Map.of(), "request");
        var locale = Locale.of("ru");

        doThrow(new UserAlreadyExistsException("users.register.errors.username_already_exists"))
                .when(this.authenticationService).register(request);

        doReturn("Username already exists")
                .when(this.messageSource).getMessage(eq("users.register.errors.username_already_exists"), any(), any(), eq(locale));

        // when
        var result = this.controller.register(request,
                bindingResult, locale);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals(HttpStatus.CONFLICT.value(), result.getBody().getStatus());
        assertEquals("Username already exists", result.getBody().getDetail());

        verify(this.authenticationService).register(request);
    }

    @Test
    @DisplayName("authenticate вернёт токен")
    void authenticate_RequestIsValid_ReturnsAuthenticationResponse() throws BindException {
        // given
        var request = new AuthenticationRequest("username", "password");
        var bindingResult = new MapBindingResult(Map.of(), "request");
        var locale = Locale.of("ru");
        var response = new AuthenticationResponse("token");

        doReturn(response)
                .when(this.authenticationService).authenticate(request);

        // when
        var result = this.controller.authenticate(request,
                bindingResult, locale);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(this.authenticationService).authenticate(request);
        verifyNoMoreInteractions(this.authenticationService);
    }

    @Test
    @DisplayName("authenticate преобразует исключение к BindException и выбросит его")
    void authenticate_RequestIsInvalid_ThrowsBindException() {
        // given
        var request = new AuthenticationRequest("", "password");
        var bindingResult = new MapBindingResult(Map.of(), "request");
        bindingResult.addError(new FieldError("request", "username", "error"));
        var locale = Locale.of("ru");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.authenticate(request,
                        bindingResult, locale));

        // then
        assertEquals(List.of(new FieldError("request", "username",
                "error")), exception.getAllErrors());
        verifyNoInteractions(this.authenticationService);
    }

    @Test
    @DisplayName("authenticate выбросит BindException")
    void authenticate_RequestIsInvalidAndBindResultIsBindException_ThrowsBindException() {
        // given
        var request = new AuthenticationRequest("", "password");
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "request"));
        bindingResult.addError(new FieldError("request", "username", "error"));
        var locale = Locale.of("ru");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.authenticate(request,
                        bindingResult, locale));

        // then
        assertEquals(List.of(new FieldError("request", "username",
                "error")), exception.getAllErrors());
        verifyNoInteractions(this.authenticationService);
    }

    @Test
    @DisplayName("authenticate вернёт Unauthorized")
    void authenticate_BadCredentials_ReturnsUnauthorized() throws BindException {
        // given
        var request = new AuthenticationRequest("username", "password");
        var bindingResult = new MapBindingResult(Map.of(), "request");
        var locale = Locale.of("ru");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(this.authenticationService).authenticate(request);

        doReturn("Bad credentials")
                .when(this.messageSource).getMessage(eq("Bad credentials"), any(), any(), eq(locale));

        // when
        var result = this.controller.authenticate(request,
                bindingResult, locale);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());

        ProblemDetail responseBody = (ProblemDetail) result.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseBody.getStatus());
        assertEquals("Bad credentials", responseBody.getDetail());

        verify(this.authenticationService).authenticate(request);
    }
}