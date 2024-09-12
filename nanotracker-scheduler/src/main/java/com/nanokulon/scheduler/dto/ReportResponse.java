package com.nanokulon.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportResponse {

    private String email;

    private String title;

    private String body;
}
