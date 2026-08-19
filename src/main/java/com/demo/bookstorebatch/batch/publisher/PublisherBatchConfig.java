package com.demo.bookstorebatch.batch.publisher;

import com.demo.bookstorebatch.model.Publisher;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class PublisherBatchConfig {
    private final EntityManagerFactory entityManager;

    @Bean
    public Job publisherJob(
            JobRepository jobRepository,
            Step publisherStep) {
        return new JobBuilder(
                "publisherJob",
                jobRepository
        )
                .start(publisherStep)
                .build();
    }

    @Bean
    public Step publisherStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Publisher> publisherReader,
            ItemProcessor<Publisher, Publisher> publisherProcessor,
            ItemWriter<Publisher> publisherWriter
    ) {
        return new StepBuilder(
                "publisherStep",
                jobRepository
        )
                .<Publisher, Publisher>chunk(100)
                .transactionManager(transactionManager)
                .reader(publisherReader)
                .processor(publisherProcessor)
                .writer(publisherWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Publisher> publisherReader(
            @Value("#{jobParameters['file']}") String file) {
        BeanWrapperFieldSetMapper<Publisher> mapper = new BeanWrapperFieldSetMapper<>();

        mapper.setTargetType(Publisher.class);

        return new FlatFileItemReaderBuilder<Publisher>()
                .name("publisherReader")
                .resource(new FileSystemResource(file))
                .delimited()
                .names("name", "address")
                .fieldSetMapper(mapper)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<Publisher, Publisher> publisherProcessor() {
        return publisher -> {
            publisher.setAddress(publisher.getAddress());
            publisher.setName(publisher.getName());

            return publisher;
        };
    }

    @Bean
    public JpaItemWriter<Publisher> publisherWriter() {
        return new JpaItemWriter<>(entityManager);
    }
}
