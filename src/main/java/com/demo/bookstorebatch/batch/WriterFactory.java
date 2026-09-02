package com.demo.bookstorebatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;

@RequiredArgsConstructor
public final class WriterFactory {
    public static <T> JpaItemWriter<T> create(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriter<>(entityManagerFactory);
    }
}
