package com.demo.bookstorebatch.batch.book;

import com.demo.bookstorebatch.model.Book;
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
public class BookBatchConfig {
    private final EntityManagerFactory entityManager;

    @Bean
    public Job bookJob(
            JobRepository jobRepository,
            Step bookStep ) {
        return new JobBuilder(
                "bookJob",
                jobRepository
        )
                .start(bookStep)
                .build();
    }

    @Bean
    public Step bookStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Book> bookReader,
            ItemProcessor<Book, Book> bookProcessor,
            ItemWriter<Book> bookWriter
    ) {
        return  new StepBuilder(
                "bookStep",
                jobRepository
        )
                .<Book, Book>chunk(100)
                .transactionManager(transactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Book> bookReader(
            @Value("#{jobParameters['file']}") String file) {
        BeanWrapperFieldSetMapper<Book> mapper = new BeanWrapperFieldSetMapper<>();

        mapper.setTargetType(Book.class);

        return new FlatFileItemReaderBuilder<Book>()
                .name("bookReader")
                .resource(new FileSystemResource(file))
                .delimited()
                .names("title", "isbn", "authors")
                .fieldSetMapper(mapper)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<Book, Book> bookProcessor() {
        return book -> {
            book.setTitle(book.getTitle());
            book.setIsbn(book.getIsbn());
            book.setAuthors(book.getAuthors());

            return book;
        };
    }

    @Bean
    public JpaItemWriter<Book> bookWriter() {
        return new JpaItemWriter<>(entityManager);
    }
}
