package com.nanokulon.mailsender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanokulon.mailsender.dto.MailDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class);

    @Value("${mail.sender}")
    private String sender;

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    public void convertAndSend(String message) {
        try {
            MailDto mailDto = this.objectMapper.readValue(message, MailDto.class);
            this.sendEmail(mailDto.getEmail(), mailDto.getSubject(), mailDto.getBody());
        } catch (JsonProcessingException exception) {
            logger.error(exception.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
