package com.demo.bookstorebatch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class FtpBatchJobLauncher {
    private final JobLauncher jobLauncher;
    private final Job importEntityJob;

    public void launch(File file) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("entity", resolveEntity(file))
                .addString("file", file.getAbsolutePath())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(importEntityJob, jobParameters);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not launch batch job for "
                            + file.getName(), e);
        }
    }

    private String resolveEntity(File file) {
        String filename = file.getName().toLowerCase();

        if (filename.startsWith("publishers")) {
            return "publisher";
        }
        if (filename.startsWith("authors")) {
            return "author";
        }
        if (filename.startsWith("books")) {
            return "book";
        }

        throw new IllegalArgumentException(
                "Unknown batch file: " + filename
        );
    }
}
