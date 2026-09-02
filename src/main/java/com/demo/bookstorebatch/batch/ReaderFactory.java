package com.demo.bookstorebatch.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.FileSystemResource;

@RequiredArgsConstructor
public final class ReaderFactory {
    public static <T> FlatFileItemReader<T> create(
            Class<T> type,
            String[] columns,
            String file) {
        BeanWrapperFieldSetMapper<T> mapper =
                new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(type);

        return new FlatFileItemReaderBuilder<T>()
                .name(type.getSimpleName() + "Reader")
                .resource(new FileSystemResource(file))
                .delimited()
                .names(columns)
                .fieldSetMapper(mapper)
                .linesToSkip(1)
                .build();
    }
}
