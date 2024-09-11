package com.nanokulon.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NanoTrackerSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NanoTrackerSchedulerApplication.class, args);
    }
}
