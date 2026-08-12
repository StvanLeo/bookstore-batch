package com.demo.bookstorebatch.config;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.util.function.UnaryOperator;

@RequiredArgsConstructor
public class GenericProcessor<T> implements ItemProcessor<T, T> {
    private final UnaryOperator<T> operation;

    @Override
    public @Nullable T process(@NonNull T item) throws Exception {
        return operation.apply(item);
    }
}
