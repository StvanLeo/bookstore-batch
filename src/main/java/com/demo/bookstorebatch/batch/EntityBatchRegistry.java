package com.demo.bookstorebatch.batch;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EntityBatchRegistry {
    private final Map<String, EntityBatchDefinition<?>> definitions;

    public EntityBatchRegistry(List<EntityBatchDefinition<?>> definitions) {
        this.definitions = definitions
                .stream()
                .collect(Collectors.toMap(
                        EntityBatchDefinition::name,
                        Function.identity()
                ));
    }

    public EntityBatchDefinition<?> get(String name) {
        EntityBatchDefinition<?> definition =
                definitions.get(name.toLowerCase());

        if (definition == null) {
            throw new IllegalArgumentException(
                    "No batch definition found for: " + name);
        }

        return definition;
    }
}
