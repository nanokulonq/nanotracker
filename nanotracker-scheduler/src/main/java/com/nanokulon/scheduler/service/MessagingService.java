package com.nanokulon.scheduler.service;

import com.nanokulon.scheduler.dto.MailDto;

public interface MessagingService {

    void convertAndSend(MailDto message);
}
