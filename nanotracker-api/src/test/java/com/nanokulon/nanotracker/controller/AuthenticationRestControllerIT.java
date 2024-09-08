package com.nanokulon.nanotracker.controller;

import com.jayway.jsonpath.JsonPath;
import com.nanokulon.nanotracker.security.TrackerUserDetails;
import com.nanokulon.nanotracker.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Интеграционные тесты AuthenticationRestController")
class AuthenticationRestControllerIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("register вернёт NoContent")
    void register_RequestIsValid_ReturnsNoContent() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "newuser@example.com",
                         "username": "newuser",
                         "password": "1Password"
                        }""");

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @DisplayName("register вернёт BadRequest")
    void register_RequestIsInvalid_ReturnsBadRequest() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "newuser@example.com",
                         "username": "newuser",
                         "password": "password"
                        }""");

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "errors": [
                                "Password must contain at least one uppercase letter and one digit"
                                ]}""")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @DisplayName("register вернёт Conflict")
    void register_UserAlreadyExists_ReturnsConflict() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "newuser@example.com",
                         "username": "testuser",
                         "password": "1Password"
                        }""");

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "detail":
                                "Username already exists"
                                }""")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @DisplayName("register вернёт Forbidden")
    void register_UserAlreadyAuthenticated_ReturnsForbidden() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "1Password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "newuser@example.com",
                         "username": "newuser",
                         "password": "1Password"
                        }""")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @DisplayName("authenticate вернёт токен")
    void authenticate_RequestIsValid_ReturnsToken() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "testuser",
                         "password": "1Password"
                        }""");

        // when
        MvcResult result = mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").exists()
                )
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        String token = JsonPath.read(jsonResponse, "$.token");
        assertEquals("testuser", jwtService.getUsername(token));
    }

    @Test
    @Sql("/sql/user.sql")
    @DisplayName("authenticate вернёт BadRequest")
    void authenticate_RequestIsInvalid_ReturnsBadRequest() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "",
                         "password": "1Password"
                        }""");

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "errors": [
                                "Specify the username"
                                ]}""")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @DisplayName("authenticate вернёт Unauthorized")
    void authenticate_BadCredentials_ReturnsUnauthorized() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "testuser",
                         "password": "incorrect1Password"
                        }""");

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "detail":
                                "Bad credentials"
                                }""")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @DisplayName("authenticate вернёт Forbidden")
    void authenticate_UserAlreadyAuthenticated_ReturnsForbidden() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "1Password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "testuser",
                         "password": "incorrect1Password"
                        }""")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}