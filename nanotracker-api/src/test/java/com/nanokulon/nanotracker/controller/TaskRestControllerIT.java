package com.nanokulon.nanotracker.controller;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Интеграционные тесты TaskRestController")
class TaskRestControllerIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("findTask вернёт задачу")
    void findTask_TaskExists_ReturnsTask() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/tasks/1")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id": 1, "title": "Задача 1", "details": "Описание задачи 1", "isCompleted": false}
                                """)
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("findTask вернёт NotFound")
    void findTask_TaskDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/tasks/5")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "detail": "Task not found"
                                }""")
                );
    }

    @Test
    @DisplayName("findTask вернёт Unauthorized")
    void findTask_UserIsNotAuthenticated_ReturnsUnauthorized() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/tasks/1");

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("findTask вернёт Forbidden")
    void findTask_TaskDoesNotBelongToUser_ReturnsForbidden() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(2, "testuser1@example.com",
                "testuser1", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/tasks/1")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "detail": "Task doesn't belong to user"
                                }""")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("updateTask вернёт NoContent")
    void updateTask_RequestIsValid_ReturnsNoContent() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Название",
                        "details": "Описание"
                        }""")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("updateTask вернёт BadRequest")
    void updateTask_RequestIsInvalid_ReturnsBadRequest() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "",
                        "details": "Описание"
                        }""")
                .header("Authorization", "Bearer " + token);

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
                                "Specify the task name",
                                "The task title must be between 3 and 100 characters"
                                ]}""")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("updateTask вернёт NotFound")
    void updateTask_TaskDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/tasks/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Название",
                        "details": "Описание"
                        }""")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "detail": "Task not found"
                                }""")
                );
    }

    @Test
    @DisplayName("updateTask вернёт Unauthorized")
    void updateTask_UserIsNotAuthenticated_ReturnsUnauthorized() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/tasks/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Название",
                        "details": "Описание"
                        }""");

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("updateTask вернёт Forbidden")
    void updateTask_TaskDoesNotBelongToUser_ReturnsForbidden() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(2, "testuser1@example.com",
                "testuser1", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.patch("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Название",
                        "details": "Описание"
                        }""")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "detail": "Task doesn't belong to user"
                                }""")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("deleteTask вернёт NoContent")
    void deleteTask_TaskExists_ReturnsNoContent() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/tasks/1")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("deleteTask вернёт NotFound")
    void deleteTask_TaskDoesNotExist_ReturnsNotFound() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/tasks/5")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "detail": "Task not found"
                                }""")
                );
    }

    @Test
    @DisplayName("deleteTask вернёт Unauthorized")
    void deleteTask_UserIsNotAuthenticated_ReturnsUnauthorized() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/tasks/1");

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("deleteTask вернёт Forbidden")
    void deleteTask_TaskDoesNotBelongToUser_ReturnsForbidden() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(2, "testuser1@example.com",
                "testuser1", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.delete("/api/v1/tasks/1")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "detail": "Task doesn't belong to user"
                                }""")
                );
    }
}