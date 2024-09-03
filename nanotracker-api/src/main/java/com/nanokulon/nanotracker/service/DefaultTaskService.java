package com.nanokulon.nanotracker.service;

import com.nanokulon.nanotracker.dto.request.TaskCreateRequest;
import com.nanokulon.nanotracker.dto.request.TaskUpdateRequest;
import com.nanokulon.nanotracker.dto.response.TaskResponse;
import com.nanokulon.nanotracker.entity.Task;
import com.nanokulon.nanotracker.entity.TrackerUser;
import com.nanokulon.nanotracker.exception.TaskOwnershipException;
import com.nanokulon.nanotracker.mapper.TaskListMapper;
import com.nanokulon.nanotracker.mapper.TaskMapper;
import com.nanokulon.nanotracker.repository.TaskRepository;
import com.nanokulon.nanotracker.repository.TrackerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DefaultTaskService implements TaskService {

    private final TaskRepository taskRepository;
    private final TrackerUserRepository trackerUserRepository;
    private final TaskListMapper taskListMapper;
    private final TaskMapper taskMapper;

    @Override
    @Transactional(readOnly = true)
    public TaskResponse findTask(int taskId, int userId) {
        return this.taskMapper
                .toTaskResponse(this.getTaskAndCheckOwnership(taskId, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> findAllTasksByUserId(int id) {
        return taskListMapper.toTaskResponse(
                this.taskRepository.findAllByUserId(id));
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskCreateRequest taskCreateRequest, int userId) {
        TrackerUser user = trackerUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Task task = taskMapper.toTask(taskCreateRequest,
                user, LocalDateTime.now(), false);
        this.taskRepository.save(task);
        return this.taskMapper.toTaskResponse(task);
    }

    @Override
    @Transactional
    public void updateTask(int taskId, TaskUpdateRequest taskUpdateRequest, int userId) {
        Task task = this.getTaskAndCheckOwnership(taskId, userId);
        task.setTitle(taskUpdateRequest.getTitle());
        task.setDetails(taskUpdateRequest.getDetails());
        if (taskUpdateRequest.getIsCompleted() != null &&
                task.getIsCompleted() != taskUpdateRequest.getIsCompleted()) {
           task.setIsCompleted(taskUpdateRequest.getIsCompleted());
           task.setCompletedDate(taskUpdateRequest.getIsCompleted() ? LocalDateTime.now() : null);
        }
    }

    @Override
    @Transactional
    public void deleteTask(int taskId, int userId) {
        this.getTaskAndCheckOwnership(taskId, userId);
        this.taskRepository.deleteById(taskId);
    }

    private Task getTaskAndCheckOwnership(int taskId, int userId) {
        Task task = this.getTaskById(taskId);
        this.checkTaskOwnership(task.getUser().getId(), userId);
        return task;
    }

    private Task getTaskById(int taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));
    }

    private void checkTaskOwnership(int taskId, int userId) {
        if (taskId != userId) {
            throw new TaskOwnershipException("Task doesn't belong to user");
        }
    }
}
