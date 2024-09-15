package com.nanokulon.scheduler.util;

import com.nanokulon.scheduler.dto.MailDto;
import com.nanokulon.scheduler.entity.Task;
import com.nanokulon.scheduler.entity.TrackerUser;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ReportResponseFactory {

    private static final String COMPLETED_TASKS_COUNT_MESSAGE =
            "Поздравляю! За сегодня вы выполнили %d задач!\n";
    private static final String MOTIVATIONAL_MESSAGE_NO_TASKS_COMPLETED =
            "За сегодня вы выполнили 0 задач! Хватит валяться на диване! Пора браться за работу!";
    private static final String REPORT_TITLE_MESSAGE =
            "%s! Посмотрите отчёт о проделанной работе.";

    public MailDto create(TrackerUser user) {
        return new MailDto(
                user.getEmail(),
                createReportTitle(user.getUsername()),
                createReportBody(user)
        );
    }

    private String createReportTitle(String username) {
        return REPORT_TITLE_MESSAGE.formatted(username);
    }

    private String createReportBody(TrackerUser user) {
        StringBuilder sb = new StringBuilder();
        appendTasksSummary(user.getTasks(), sb);
        return sb.toString();
    }

    private void appendTasksSummary(List<Task> tasks, StringBuilder sb) {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<String> completedTasks = tasks.stream()
                .filter(Task::getIsCompleted)
                .filter(task -> task.getCompletedDate().isAfter(twentyFourHoursAgo))
                .map(Task::getTitle)
                .toList();

        if (!completedTasks.isEmpty()) {
            sb.append(COMPLETED_TASKS_COUNT_MESSAGE.formatted(completedTasks.size()));
            completedTasks.forEach(task -> sb.append("— ").append(task).append("\n"));
        } else {
            sb.append(MOTIVATIONAL_MESSAGE_NO_TASKS_COMPLETED);
        }
    }
}
