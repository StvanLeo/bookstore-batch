package com.demo.bookstorebatch.batch;

import org.springframework.batch.infrastructure.item.ItemProcessor;

public interface EntityBatchDefinition<T> {
        String name();
        Class<T> entityType();
        String[] csvColumns();
        ItemProcessor<T, T> processor();
}
