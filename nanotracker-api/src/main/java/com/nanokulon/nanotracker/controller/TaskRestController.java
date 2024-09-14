package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.annotation.ApiDocumentationAnnotations;
import com.nanokulon.nanotracker.dto.request.TaskUpdateRequest;
import com.nanokulon.nanotracker.dto.response.TaskResponse;
import com.nanokulon.nanotracker.exception.TaskOwnershipException;
import com.nanokulon.nanotracker.security.TrackerUserDetails;
import com.nanokulon.nanotracker.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Tag(name = "Планировщик задач", description = "Точка входа для работы со списком задач пользователя")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/{taskId:\\d+}")
public class TaskRestController {

    private final TaskService taskService;
    private final MessageSource messageSource;

    @ApiDocumentationAnnotations.OperationFindTask
    @GetMapping
    public ResponseEntity<TaskResponse> findTask(@PathVariable int taskId,
                                                 @AuthenticationPrincipal TrackerUserDetails userDetails) {
        return ResponseEntity.ok(
                this.taskService.findTask(taskId, userDetails.getId()));
    }

    @ApiDocumentationAnnotations.OperationUpdateTask
    @PatchMapping
    public ResponseEntity<?> updateTask(@PathVariable("taskId") int taskId,
                                        @Valid @RequestBody TaskUpdateRequest taskUpdateRequest,
                                        @AuthenticationPrincipal TrackerUserDetails userDetails,
                                        BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.taskService.updateTask(taskId, taskUpdateRequest, userDetails.getId());
            return ResponseEntity.noContent()
                    .build();
        }

    }

    @ApiDocumentationAnnotations.OperationDeleteTask
    @DeleteMapping
    public ResponseEntity<Void> deleteTask(@PathVariable("taskId") int taskId,
                                           @AuthenticationPrincipal TrackerUserDetails userDetails) {
        this.taskService.deleteTask(taskId, userDetails.getId());
        return ResponseEntity.noContent()
                .build();
    }


    @ExceptionHandler(TaskOwnershipException.class)
    public ResponseEntity<ProblemDetail> handleTaskOwnershipException(TaskOwnershipException exception,
                                                                      Locale locale) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                        this.messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale)));
    }
}
