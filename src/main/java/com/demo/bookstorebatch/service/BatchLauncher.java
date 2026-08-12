package com.demo.bookstorebatch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class BatchLauncher {
    private final JobOperator jobOperator;
    private final Job publisherJob;

    private void launch(Path file) throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        Job job = switch(file.getFileName().toString()) {
            case "pub.csv" -> publisherJob;
            default -> throw new IllegalArgumentException();
        };

        JobParameters params =
                new JobParametersBuilder()
                        .addString("file", file.toString())
                        .addLong("timestamp", System.currentTimeMillis())
                        .toJobParameters();

        jobOperator.start(job, params);
    }
}
