package com.demo.bookstorebatch.definition;

import com.demo.bookstorebatch.batch.EntityBatchDefinition;
import com.demo.bookstorebatch.exception.DuplicateResourceException;
import com.demo.bookstorebatch.model.Publisher;
import com.demo.bookstorebatch.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublisherBatchDefinition implements EntityBatchDefinition<Publisher> {
    private final PublisherRepository publisherRepository;

    @Override
    public String name() {
        return "publisher";
    }

    @Override
    public Class<Publisher> entityType() {
        return Publisher.class;
    }

    @Override
    public String[] csvColumns() {
        return new String[] {
                "name",
                "address"
        };
    }

    @Override
    public ItemProcessor<Publisher, Publisher> processor() {
        return publisher -> {
            publisher.setName(publisher.getName());
            publisher.setAddress(publisher.getAddress());

            Example<Publisher> publisherExample = Example.of(publisher);

            if(publisherRepository.exists(publisherExample)) {
                throw new DuplicateResourceException("Publisher", publisher.getName());
            }

            return publisher;
        };
    }
}
