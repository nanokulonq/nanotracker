package com.nanokulon.scheduler.service;

import com.nanokulon.scheduler.entity.TrackerUser;
import com.nanokulon.scheduler.repository.TrackerUserRepository;
import com.nanokulon.scheduler.util.ReportResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final TrackerUserRepository trackerUserRepository;
    private final KafkaMessagingService kafkaMessagingService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional(readOnly = true)
    public void processAndSendDailyUserReports() {
        List<TrackerUser> users = trackerUserRepository.findUsersWithTasksCompletedToday();
        for (TrackerUser user : users) {
            kafkaMessagingService.convertAndSend(ReportResponseFactory.create(user));
        }
    }
}
