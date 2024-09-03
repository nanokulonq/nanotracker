package com.nanokulon.nanotracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskResponse {
    private int id;
    private String title;
    private String details;
    private Boolean isCompleted;
}
