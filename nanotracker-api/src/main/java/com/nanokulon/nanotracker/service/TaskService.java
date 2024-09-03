package com.nanokulon.nanotracker.service;

import com.nanokulon.nanotracker.dto.request.TaskCreateRequest;
import com.nanokulon.nanotracker.dto.request.TaskUpdateRequest;
import com.nanokulon.nanotracker.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse findTask(int taskId, int userId);

    List<TaskResponse> findAllTasksByUserId(int id);

    TaskResponse createTask(TaskCreateRequest taskCreateRequest, int userId);

    void updateTask(int taskId, TaskUpdateRequest taskUpdateRequest, int userId);

    void deleteTask(int taskId, int userId);
}
