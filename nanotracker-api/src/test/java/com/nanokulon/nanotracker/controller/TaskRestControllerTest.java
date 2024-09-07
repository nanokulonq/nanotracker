package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.dto.request.TaskUpdateRequest;
import com.nanokulon.nanotracker.dto.response.TaskResponse;
import com.nanokulon.nanotracker.exception.TaskOwnershipException;
import com.nanokulon.nanotracker.security.TrackerUserDetails;
import com.nanokulon.nanotracker.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты TaskRestController")
class TaskRestControllerTest {

    @Mock
    TaskService taskService;
    @Mock
    MessageSource messageSource;
    @Mock
    TrackerUserDetails userDetails;
    @InjectMocks
    TaskRestController controller;

    @Test
    @DisplayName("findTask вернёт задачу")
    void findTask_TaskExists_ReturnsTask() {
        // given
        var response = new TaskResponse(1, "Задача 1",
                "Описание задачи 1", false);

        doReturn(1)
                .when(this.userDetails)
                .getId();

        doReturn(response)
                .when(this.taskService).findTask(1, 1);

        // when
        var result = this.controller.findTask(1, this.userDetails);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(this.taskService).findTask(1, 1);
        verifyNoMoreInteractions(this.taskService);
    }

    @Test
    @DisplayName("updateTask обновит задачу и вернёт NoContent")
    void updateTask_RequestIsValid_ReturnsNoContent() throws BindException {
        // given
        var taskUpdateRequest = new TaskUpdateRequest("Название", "Описание", true);
        var bindingResult = new MapBindingResult(Map.of(), "taskUpdateRequest");

        doReturn(1)
                .when(this.userDetails)
                .getId();

        // when
        var result = this.controller.updateTask(1, taskUpdateRequest,
                this.userDetails, bindingResult);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.taskService).updateTask(1, taskUpdateRequest, 1);
        verifyNoMoreInteractions(this.taskService);
    }

    @Test
    @DisplayName("updateTask преобразует исключение к BindException и выбросит его")
    void updateTask_RequestIsInvalid_ReturnsNoContent() {
        // given
        var taskUpdateRequest = new TaskUpdateRequest("", "Описание", true);
        var bindingResult = new MapBindingResult(Map.of(), "taskUpdateRequest");
        bindingResult.addError(new FieldError("taskUpdateRequest", "title", "error"));

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.updateTask(1, taskUpdateRequest,
                        this.userDetails, bindingResult));

        // then
        assertEquals(List.of(new FieldError("taskUpdateRequest", "title",
                "error")), exception.getAllErrors());
        verifyNoInteractions(this.taskService);
    }

    @Test
    @DisplayName("updateTask выбросит BindException")
    void updateTask_RequestIsInvalidAndBindResultIsBindException_ReturnsNoContent() {
        // given
        var taskUpdateRequest = new TaskUpdateRequest("", "Описание", true);
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "taskUpdateRequest"));
        bindingResult.addError(new FieldError("taskUpdateRequest", "title", "error"));

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.updateTask(1, taskUpdateRequest,
                        this.userDetails, bindingResult));

        // then
        assertEquals(List.of(new FieldError("taskUpdateRequest", "title",
                "error")), exception.getAllErrors());
        verifyNoInteractions(this.taskService);
    }

    @Test
    @DisplayName("deleteTask вернёт NoContent")
    void deleteTask_ReturnsNoContent() {
        // given
        doReturn(1)
                .when(this.userDetails)
                .getId();

        // when
        var result = this.controller.deleteTask(1, this.userDetails);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.taskService).deleteTask(1, 1);
        verifyNoMoreInteractions(this.taskService);
    }

    @Test
    void handleTaskOwnershipException_ReturnsForbidden() {
        // given
        var exception = new TaskOwnershipException("error_code");
        var locale = Locale.of("ru");

        doReturn("error details").when(this.messageSource)
                .getMessage("error_code", new Object[0],
                        "error_code", Locale.of("ru"));

        // when
        var result = this.controller.handleTaskOwnershipException(exception, locale);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals(HttpStatus.FORBIDDEN.value(), result.getBody().getStatus());
        assertEquals("error details", result.getBody().getDetail());

        verifyNoMoreInteractions(this.taskService);
    }
}