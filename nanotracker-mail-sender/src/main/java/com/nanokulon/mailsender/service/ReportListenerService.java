package com.nanokulon.mailsender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportListenerService {

    private final EmailSenderService emailSenderService;

    @KafkaListener(topics = "report-topic", groupId = "report-consumer-group")
    public void listen(String message) {
        emailSenderService.convertAndSend(message);
        System.out.println(message);
    }
}
