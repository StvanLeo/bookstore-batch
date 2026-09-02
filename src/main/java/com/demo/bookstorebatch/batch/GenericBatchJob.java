package com.demo.bookstorebatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class GenericBatchJob {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityBatchRegistry registry;

    @Bean
    public Job importEntityJob(JobRepository jobRepository,
                               Step importEntityStep) {
        return new JobBuilder(
                "importEntityJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importEntityStep)
                .build();
    }

    @Bean
    @JobScope
    public Step importEntityStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Value("#{jobParameters['entity']}") String entity,
            @Value("#{jobParameters['file']}") String file) {

        if (entity == null || entity.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required job parameter: entity");
        }

        if (file == null || file.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required job parameter: file");
        }

        EntityBatchDefinition<?> definition =
                registry.get(entity);

        return createStep(
          jobRepository,
          transactionManager,
          definition,
          file
        );
    }

    @SuppressWarnings("unchecked")
    private <T> Step createStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            EntityBatchDefinition<?> definition,
            String file) {

        EntityBatchDefinition<T> typeDefinition =
                (EntityBatchDefinition<T>) definition;

        ItemReader<T> reader =
                ReaderFactory.create(
                        typeDefinition.entityType(),
                        typeDefinition.csvColumns(),
                        file
                );

        ItemProcessor<T, T> processor =
                typeDefinition.processor();


        ItemWriter<T> writer =
                WriterFactory.create(
                     entityManagerFactory
                );

        return new StepBuilder(
                definition.name() + "Step",
                jobRepository
        )
                .<T, T>chunk(10)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
