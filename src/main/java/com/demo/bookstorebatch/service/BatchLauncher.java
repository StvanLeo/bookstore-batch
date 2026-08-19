package com.demo.bookstorebatch.service;

import com.demo.bookstorebatch.batch.ImportDefinition;
import com.demo.bookstorebatch.batch.ImportRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class BatchLauncher {
    private final JobOperator jobOperator;
    private final ImportRegistry importRegistry;

    public void launch(File file) {
        ImportDefinition definition =
                importRegistry.find(file);

        JobParameters parameters = new JobParametersBuilder()
                .addString(
                        "file",
                        file.getAbsolutePath(),
                        true
                )
                .addString(
                        "filename",
                        file.getName(),
                        true
                )
                .toJobParameters();

        try {
            jobOperator.start(
                    definition.job(),
                    parameters
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not launch batch for " + file + e);
        }
    }
}
