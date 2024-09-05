package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.security.TrackerUserDetails;
import com.nanokulon.nanotracker.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Интеграционные тесты TasksRestController")
class TasksRestControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;

    @Test
    @Sql("/sql/user.sql")
    @Sql("/sql/task.sql")
    @DisplayName("findUserTasks вернёт список задач пользователя")
    void findUserTasks_ReturnsUserTasks() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/tasks")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                {"id": 1, "title": "Задача 1", "details": "Описание задачи 1", "isCompleted": false},
                                {"id": 2, "title": "Задача 2", "details": "Описание задачи 2", "isCompleted": false},
                                {"id": 3, "title": "Задача 3", "details": "Описание задачи 3", "isCompleted": false}
                                ]""")
                );
    }

    @Test
    @DisplayName("findUserTasks вернёт статус 401")
    void findUserTasks_UserIsNotAuthenticated_ReturnsUnauthorized() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/v1/tasks");

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
    @DisplayName("createTask создаст задачу и вернёт эту задачу с URL этой задачи")
    void createTask_RequestIsValid_ReturnsCreatedTask() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Новая задача", "details": "Описание новой задачи"}""")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/api/v1/tasks/1"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {"id": 1,
                                "title": "Новая задача",
                                "details": "Описание новой задачи",
                                "isCompleted": false}""")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    @DisplayName("createTask вернёт BadRequest с problem detail")
    void createTask_RequestIsInvalid_ReturnsBadRequest() throws Exception {
        // given
        UserDetails userDetails = new TrackerUserDetails(1, "testuser@example.com",
                "testuser", "testpassword", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        var requestBuilder = MockMvcRequestBuilders.post("/api/v1/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "", "details": "New Task Details"}""")
                .header("Authorization", "Bearer " + token);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "errors": [
                                "Specify the task name",
                                "The task title must be between 3 and 100 characters"
                                ]}""")
                );
    }
}
