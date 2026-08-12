package com.demo.bookstorebatch.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

public class BatchFactory {
    public static <T> FlatFileItemReader<T> reader(
            Class<T> targetClass,
            Resource resource,
            String... columns) {
        BeanWrapperFieldSetMapper<T> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(targetClass);

        return new FlatFileItemReaderBuilder<T>()
                .name(targetClass.getSimpleName() + "Reader")
                .resource(resource)
                .delimited()
                .names(columns)
                .fieldSetMapper(mapper)
                .linesToSkip(1)
                .build();
    }

    public static <T> JpaItemWriter<T> writer(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriter<>(entityManagerFactory);
    }

    public static <T> Step createStep(
            JobRepository jobRepository,
            PlatformTransactionManager txManager,
            ItemReader<T> reader,
            ItemProcessor<T, T> processor,
            ItemWriter<T> writer,
            String name) {

        return new StepBuilder(name, jobRepository)
                .<T,T>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(txManager)
                .build();
    }
}
