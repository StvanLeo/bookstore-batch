package com.demo.bookstorebatch.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class ImportRegistry {
    private final List<ImportDefinition> definitions;

    public ImportRegistry(
            @Qualifier("publisherJob") Job publisherJob,
            @Qualifier("authorJob") Job authorJob,
            @Qualifier("bookJob") Job bookJob) {
        this.definitions = List.of(
                new ImportDefinition(
                        "publishers",
                        publisherJob),
                new ImportDefinition(
                        "authors",
                        authorJob
                ),
                new ImportDefinition(
                        "book",
                        bookJob
                )
        );
    }

    public ImportDefinition find(File file) {
        String filename = file.getName().toLowerCase();

        return definitions.stream()
                .filter(definition ->
                        filename.startsWith(definition.filePrefix()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No import definition found for: " + filename
                        ));
    }
}
