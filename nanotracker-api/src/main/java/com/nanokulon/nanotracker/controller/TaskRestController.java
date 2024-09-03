package com.nanokulon.nanotracker.controller;

import com.nanokulon.nanotracker.dto.request.TaskUpdateRequest;
import com.nanokulon.nanotracker.dto.response.TaskResponse;
import com.nanokulon.nanotracker.exception.TaskOwnershipException;
import com.nanokulon.nanotracker.security.TrackerUserDetails;
import com.nanokulon.nanotracker.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/{taskId:\\d+}")
public class TaskRestController {

    private final TaskService taskService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<TaskResponse> getTask(@PathVariable int taskId,
                                                @AuthenticationPrincipal TrackerUserDetails userDetails) {
        return ResponseEntity.ok(
                this.taskService.findTask(taskId, userDetails.getId()));
    }

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
