package com.demo.bookstorebatch.config;

import com.demo.bookstorebatch.batch.ReaderFactory;
import com.demo.bookstorebatch.batch.StepFactory;
import com.demo.bookstorebatch.model.Publisher;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PublisherBatchConfig {
    private final StepFactory stepFactory;

    @Bean
    public FlatFileItemReader<Publisher> publisherReader(
            @Value("#{jobParameters['file']}") String file
    ) {
        return ReaderFactory.create(
                Publisher.class,
                new String[]{
                        "name",
                        "address"
                },
                file
        );
    }

    //@Bean
    //@StepScope

}
