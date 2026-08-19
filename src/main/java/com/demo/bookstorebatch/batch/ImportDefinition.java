package com.demo.bookstorebatch.batch;

import org.springframework.batch.core.job.Job;

public record ImportDefinition(
        String filePrefix,
        Job job
) {
}
