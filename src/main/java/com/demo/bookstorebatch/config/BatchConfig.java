package com.demo.bookstorebatch.config;

import com.demo.bookstorebatch.model.Publisher;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @StepScope
    FlatFileItemReader<Publisher> reader(
            @Value("#{jobParameters['entity']}") String entity,
            @Value("#{jobParameters['file']}") String file) {
        if ("publisher".equals(entity)) {
            return BatchFactory.reader(
                    Publisher.class,
                    new FileSystemResource(file),
                    "name",
                    "address"
            );
        }

        throw new IllegalArgumentException("Unknown entity");
    }

    @Bean
    public ItemProcessor<Publisher, Publisher> publisherProcessor() {
        return new GenericProcessor<>(publisher -> {
            publisher.setAddress(publisher.getAddress().toUpperCase());
            publisher.setName(publisher.getName().toUpperCase());
            return publisher;
        });
    }

    @Bean
    public JpaItemWriter<Publisher> publisherWriter() {
        return BatchFactory.writer(entityManagerFactory);
    }

    @Bean
    Step publisherStep(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       ItemReader<Publisher> reader,
                       ItemProcessor<Publisher, Publisher> processor,
                       ItemWriter<Publisher> writer) {
        return BatchFactory.createStep(jobRepository,
                transactionManager,
                reader,
                processor,
                writer,
                "publisherStep");
    }
















    @Bean
    public Job importPublisherJob(JobRepository jobRepository, Step publisherStep) {
        return new JobBuilder("importPublisherJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(publisherStep)
                .build();
    }


}

