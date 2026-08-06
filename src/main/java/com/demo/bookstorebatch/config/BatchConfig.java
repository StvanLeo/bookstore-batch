package com.demo.bookstorebatch.config;

import com.demo.bookstorebatch.model.Publisher;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public FlatFileItemReader<Publisher> reader() {
        return new FlatFileItemReaderBuilder<Publisher>()
                .name("bookItemReader")
                .resource(new ClassPathResource("publishers.csv"))
                .delimited()
                .names(new String[]{"name", "address"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Publisher.class);
                }}).linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<Publisher, Publisher> processor() {
        return publisher -> {
            publisher.setAddress(publisher.getAddress().toUpperCase());
            publisher.setName(publisher.getName().toUpperCase());
            return publisher;
        };
    }

    @Bean
    public JpaItemWriter<Publisher> writer() {
        return new JpaItemWriter<>(entityManagerFactory);
    }

    @Bean
    public Job importPublisherJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("importPublisherJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     ItemReader<Publisher> reader,
                     ItemProcessor<Publisher, Publisher> processor,
                     ItemWriter<Publisher> writer) {

        return new StepBuilder("step", jobRepository)
                .<Publisher, Publisher>chunk(10)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
