package com.demo.bookstorebatch.config;

import com.demo.bookstorebatch.batch.StepFactory;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public StepFactory stepFactory() {
        return new StepFactory(entityManagerFactory);
    }
}

