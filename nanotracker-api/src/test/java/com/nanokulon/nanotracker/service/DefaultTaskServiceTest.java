package com.nanokulon.nanotracker.service;

import com.nanokulon.nanotracker.dto.request.TaskCreateRequest;
import com.nanokulon.nanotracker.dto.request.TaskUpdateRequest;
import com.nanokulon.nanotracker.entity.Task;
import com.nanokulon.nanotracker.entity.TrackerUser;
import com.nanokulon.nanotracker.exception.TaskOwnershipException;
import com.nanokulon.nanotracker.mapper.TaskListMapper;
import com.nanokulon.nanotracker.mapper.TaskMapper;
import com.nanokulon.nanotracker.repository.TaskRepository;
import com.nanokulon.nanotracker.repository.TrackerUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты DefaultTaskService")
class DefaultTaskServiceTest {

    @Mock
    TaskRepository taskRepository;
    @Mock
    TrackerUserRepository trackerUserRepository;
    @Mock
    TaskListMapper taskListMapper;
    @Mock
    TaskMapper taskMapper;
    @InjectMocks
    DefaultTaskService service;

    @Test
    void findTask_TaskExists_ReturnsTaskResponse() {
        // given
        var user = new TrackerUser(1, "testuser@example.com", "testuser",
                "testpassword", Collections.emptyList(), Collections.emptyList());
        var task = new Task(1, "test name", "test details", LocalDateTime.now(),
                false, null, user);
        var response = this.taskMapper.toTaskResponse(task);

        doReturn(Optional.of(task))
                .when(taskRepository).findById(1);

        // when
        var result = this.service.findTask(1, 1);

        // then
        assertEquals(response, result);

        verify(this.taskRepository).findById(1);
        verifyNoMoreInteractions(this.taskRepository);
    }

    @Test
    void findTask_TaskDoesNotExist_ThrowsNoSuchElementException() {
        // given
        doReturn(Optional.empty())
                .when(taskRepository).findById(1);

        // when
        var exception = assertThrows(NoSuchElementException.class,
                () -> this.service.findTask(1, 1));

        // then
        assertEquals("Task not found", exception.getMessage());

        verify(this.taskRepository).findById(1);
        verifyNoMoreInteractions(this.taskRepository);
    }

    @Test
    void findTask_TaskDoesNotBelongToUser_ThrowsTaskOwnershipException() {
        // given
        var user = new TrackerUser(1, "testuser@example.com", "testuser",
                "testpassword", Collections.emptyList(), Collections.emptyList());
        var task = new Task(1, "test name", "test details", LocalDateTime.now(),
                false, null, user);
        doReturn(Optional.of(task))
                .when(taskRepository).findById(1);

        // when
        var exception = assertThrows(TaskOwnershipException.class,
                () -> this.service.findTask(1, 2));

        // then
        assertEquals("Task doesn't belong to user", exception.getMessage());

        verify(this.taskRepository).findById(1);
        verifyNoMoreInteractions(this.taskRepository);
    }

    @Test
    void findAllTasksByUserId_UserExists_ReturnsTaskResponseList() {
        // given
        var user = new TrackerUser(1, "testuser@example.com", "testuser",
                "testpassword", Collections.emptyList(), Collections.emptyList());
        var task = new Task(1, "test name", "test details", LocalDateTime.now(),
                false, null, user);
        var taskList = List.of(task);
        var response = this.taskListMapper.toTaskResponse(taskList);

        doReturn(List.of(task))
                .when(taskRepository).findAllByUserId(1);

        // when
        var result = this.service.findAllTasksByUserId(1);

        // then
        assertEquals(response, result);

        verify(this.taskRepository).findAllByUserId(1);
        verifyNoMoreInteractions(this.taskRepository);
    }

    @Test
    void createTask_UserExists_ReturnsTaskResponse() {
        // given
        var user = new TrackerUser(1, "testuser@example.com", "testuser",
                "testpassword", Collections.emptyList(), Collections.emptyList());
        var request = new TaskCreateRequest("test name", "test details");
        var task = this.taskMapper.toTask(request,
                user, LocalDateTime.now(), false);
        var response = this.taskMapper.toTaskResponse(task);

        doReturn(Optional.of(user))
                .when(trackerUserRepository).findById(1);

        // when
        var result = this.service.createTask(request, 1);

        // then
        assertEquals(response, result);

        verify(this.trackerUserRepository).findById(1);
        verifyNoMoreInteractions(this.trackerUserRepository);
    }

    @Test
    void createTask_UserDoesNotExist_ThrowsNoSuchElementException() {
        // given
        var request = new TaskCreateRequest("test name", "test details");

        doReturn(Optional.empty())
                .when(trackerUserRepository).findById(1);

        // when
        var exception = assertThrows(NoSuchElementException.class,
                () -> this.service.createTask(request, 1));

        // then
        assertEquals("User not found", exception.getMessage());

        verify(this.trackerUserRepository).findById(1);
        verifyNoMoreInteractions(this.trackerUserRepository);
    }

    @Test
    void updateTask_TaskExists_UpdateTask() {
        // given
        var user = new TrackerUser(1, "testuser@example.com", "testuser",
                "testpassword", Collections.emptyList(), Collections.emptyList());
        var task = new Task(1, "test name", "test details", LocalDateTime.now(),
                false, null, user);
        var request = new TaskUpdateRequest("test name", "test details", true);

        doReturn(Optional.of(task))
                .when(taskRepository).findById(1);

        // when
        this.service.updateTask(1, request, 1);

        // then
        verify(this.taskRepository).findById(1);
        verifyNoMoreInteractions(this.taskRepository);
    }

    @Test
    void updateTask_TaskDoesNotExist_ThrowsNoSuchElementException() {
        // given
        var request = new TaskUpdateRequest("test name", "test details", true);

        doReturn(Optional.empty())
                .when(taskRepository).findById(1);

        // when
        var exception = assertThrows(NoSuchElementException.class,
                () -> this.service.updateTask(1, request, 1));

        // then
        assertEquals("Task not found", exception.getMessage());

        verify(this.taskRepository).findById(1);
        verifyNoMoreInteractions(this.taskRepository);
    }

    @Test
    void deleteTask_TaskExists_DeleteTask() {
        // given
        var user = new TrackerUser(1, "testuser@example.com", "testuser",
                "testpassword", Collections.emptyList(), Collections.emptyList());
        var task = new Task(1, "test name", "test details", LocalDateTime.now(),
                false, null, user);

        doReturn(Optional.of(task))
                .when(taskRepository).findById(1);

        // when
        this.service.deleteTask(1, 1);

        // then
        verify(this.taskRepository).findById(1);
        verify(this.taskRepository).deleteById(1);
        verifyNoMoreInteractions(this.taskRepository);
    }

    @Test
    void deleteTask_TaskDoesNotExist_ThrowsNoSuchElementException() {
        // given
        doReturn(Optional.empty())
                .when(taskRepository).findById(1);

        // when
        var exception = assertThrows(NoSuchElementException.class,
                () -> this.service.deleteTask(1, 1));

        // then
        assertEquals("Task not found", exception.getMessage());

        verify(this.taskRepository).findById(1);
        verifyNoMoreInteractions(this.taskRepository);
    }
}