package com.nanokulon.scheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanokulon.scheduler.dto.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessagingService implements MessagingService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessagingService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void convertAndSend(ReportResponse message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.sendDefault(json);
        } catch (JsonProcessingException exception) {
            logger.error(exception.getMessage());
        }
    }
}
