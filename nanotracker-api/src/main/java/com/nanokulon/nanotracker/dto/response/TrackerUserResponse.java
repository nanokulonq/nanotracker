package com.nanokulon.nanotracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TrackerUserResponse {
    private int id;
    private String username;
    private String email;
    private List<TaskResponse> tasks;
}
