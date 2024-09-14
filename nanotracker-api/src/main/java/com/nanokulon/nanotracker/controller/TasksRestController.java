package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.annotation.ApiDocumentationAnnotations;
import com.nanokulon.nanotracker.dto.request.TaskCreateRequest;
import com.nanokulon.nanotracker.dto.response.TaskResponse;
import com.nanokulon.nanotracker.security.TrackerUserDetails;
import com.nanokulon.nanotracker.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Tag(name = "Планировщик задач", description = "Точка входа для работы со списком задач пользователя")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TasksRestController {

    private final TaskService taskService;

    @ApiDocumentationAnnotations.OperationFindUserTasks
    @GetMapping
    public ResponseEntity<List<TaskResponse>> findUserTasks(
            @AuthenticationPrincipal TrackerUserDetails userDetails) {
        return ResponseEntity.ok(
                this.taskService.findAllTasksByUserId(userDetails.getId()));
    }

    @ApiDocumentationAnnotations.OperationCreateTask
    @PostMapping("/create")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskCreateRequest taskCreateRequest,
                                        @AuthenticationPrincipal TrackerUserDetails userDetails,
                                        BindingResult bindingResult,
                                        UriComponentsBuilder uriComponentsBuilder)
            throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            TaskResponse taskResponse = this.taskService
                    .createTask(taskCreateRequest, userDetails.getId());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/api/v1/tasks/{taskId}")
                            .build(Map.of("taskId", taskResponse.getId())))
                    .body(taskResponse);
        }
    }
}
