package com.demo.bookstorebatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
public class StepFactory {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public StepFactory stepFactory(EntityManagerFactory entityManagerFactory) {
        return new StepFactory(entityManagerFactory);
    }

    public <T> Step create(
            String name,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<T> reader,
            ItemProcessor<T, T> processor,
            ItemWriter<T> writer ) {
        return new StepBuilder(
                name,
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
