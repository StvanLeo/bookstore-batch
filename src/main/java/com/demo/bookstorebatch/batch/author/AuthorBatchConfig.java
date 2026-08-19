package com.demo.bookstorebatch.batch.author;

import com.demo.bookstorebatch.model.Author;
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
public class AuthorBatchConfig {
    private final EntityManagerFactory entityManager;

    @Bean
    public Job authorJob(
            JobRepository jobRepository,
            Step authorStep) {
        return new JobBuilder(
                "authorJob",
                jobRepository
        )
                .start(authorStep)
                .build();
    }

    @Bean
    public Step authorStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Author> authorReader,
            ItemProcessor<Author, Author> authorProcessor,
            ItemWriter<Author> authorWriter
    ) {
        return new StepBuilder(
                "authorStep",
                jobRepository
        )
                .<Author, Author>chunk(100)
                .transactionManager(transactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Author> authorReader(
            @Value("#{jobParameters['file']}") String file) {
        BeanWrapperFieldSetMapper<Author> mapper = new BeanWrapperFieldSetMapper<>();

        mapper.setTargetType(Author.class);

        return new FlatFileItemReaderBuilder<Author>()
                .name("authorReader")
                .resource(new FileSystemResource(file))
                .delimited()
                .names("name", "address")
                .fieldSetMapper(mapper)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<Author, Author> authorProcessor() {
        return author -> {
            author.setName(author.getName());
            author.setBiography(author.getBiography());
            author.setPublisher(author.getPublisher());

            return author;
        };
    }

    @Bean
    public JpaItemWriter<Author> authorWriter() {
        return new JpaItemWriter<>(entityManager);
    }
}
