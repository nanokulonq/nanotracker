package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.dto.request.TaskCreateRequest;
import com.nanokulon.nanotracker.dto.response.TaskResponse;
import com.nanokulon.nanotracker.security.TrackerUserDetails;
import com.nanokulon.nanotracker.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты TasksRestController")
class TasksRestControllerTest {

    @Mock
    TaskService taskService;
    @Mock
    TrackerUserDetails userDetails;
    @InjectMocks
    TasksRestController controller;

    @Test
    void findUserTasks_ReturnsUserTasks() {
        //given
        var response = List.of(new TaskResponse(1, "Задача 1", "Описание задачи 1", false),
                new TaskResponse(2, "Задача 2", "Описание задачи 2", false));

        doReturn(1)
                .when(this.userDetails)
                .getId();

        doReturn(response)
                .when(this.taskService)
                .findAllTasksByUserId(1);

        // when
        var result = this.controller.findUserTasks(userDetails);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(this.taskService).findAllTasksByUserId(1);
        verifyNoMoreInteractions(this.taskService);
    }

    @Test
    @DisplayName("createTask создаст новую задачу и вернёт эту задачу с URL этой задачи")
    void createTask_RequestIsValid_ReturnsCreatedTask() throws BindException {
        // given
        var taskCreateRequest = new TaskCreateRequest("Название", "Описание");
        var taskResponse = new TaskResponse(1, "Название", "Описание", false);
        var bindingResult = new MapBindingResult(Map.of(), "taskCreateRequest");
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doReturn(1)
                .when(this.userDetails)
                .getId();
        doReturn(taskResponse)
                .when(this.taskService)
                .createTask(taskCreateRequest, 1);

        // when
        var result = this.controller
                .createTask(taskCreateRequest, this.userDetails, bindingResult, uriComponentsBuilder);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("http://localhost/api/v1/tasks/1"), result.getHeaders().getLocation());
        assertEquals(taskResponse, result.getBody());

        verify(this.taskService).createTask(taskCreateRequest, 1);
        verifyNoMoreInteractions(this.taskService);
    }

    @Test
    void createTask_RequestIsInvalid_ReturnsBadRequest() {
        // given
        var taskCreateRequest = new TaskCreateRequest(" ", "Описание");
        var bindingResult = new MapBindingResult(Map.of(), "taskCreateRequest");
        bindingResult.addError(new FieldError("taskCreateRequest", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.createTask(taskCreateRequest, this.userDetails,
                        bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("taskCreateRequest", "title",
                "error")), exception.getAllErrors());
        verifyNoMoreInteractions(this.taskService);
    }

    @Test
    void createTask_RequestIsInvalidAndBindResultIsBindException_ReturnsBadRequest() {
        // given
        var taskCreateRequest = new TaskCreateRequest(" ", "Описание");
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "taskCreateRequest"));
        bindingResult.addError(new FieldError("taskCreateRequest", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.createTask(taskCreateRequest, this.userDetails,
                        bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("taskCreateRequest", "title",
                "error")), exception.getAllErrors());
        verifyNoMoreInteractions(this.taskService);
    }
}