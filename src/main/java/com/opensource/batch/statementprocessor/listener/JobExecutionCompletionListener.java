package com.opensource.batch.statementprocessor.listener;

import com.opensource.batch.statementprocessor.config.FileConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JobExecutionCompletionListener extends JobExecutionListenerSupport {
    @Autowired
    public FileConfig fileConfig;
    private LocalDateTime startTime;

    public void afterJob(JobExecution jobExecution) {
        LocalDateTime endTime = LocalDateTime.now();
        log.info("Ending Job : {} at {} duration : {} milliseconds and Job status is : {}", jobExecution.getJobInstance().getJobName(), endTime.toLocalTime(), TimeUnit.NANOSECONDS.toMillis(Duration.between(startTime, endTime).getNano()), jobExecution.getExitStatus());
    }

    public void beforeJob(JobExecution jobExecution) {
        startTime = LocalDateTime.now();
        log.info("Starting Job : {} at {} ", jobExecution.getJobInstance().getJobName(), startTime.toLocalTime());
    }
}
