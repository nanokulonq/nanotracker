package com.nanokulon.scheduler.service;

import com.nanokulon.scheduler.dto.ReportResponse;

public interface MessagingService {

    void convertAndSend(ReportResponse message);
}
