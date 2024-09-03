package com.nanokulon.nanotracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskUpdateRequest {
    @NotBlank(message = "{task.update.errors.title_is_null}")
    @Size(min = 3, max = 100, message = "{task.update.errors.title_size_is_invalid}")
    private String title;
    @NotBlank(message = "{task.update.errors.description_is_null}")
    @Size(max = 5000, message = "{task.update.errors.description_size_is_invalid}")
    private String details;
    private Boolean isCompleted;
}
