package com.demo.bookstorebatch.definition;

import com.demo.bookstorebatch.batch.EntityBatchDefinition;
import com.demo.bookstorebatch.model.Publisher;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class PublisherBatchDefinition implements EntityBatchDefinition<Publisher> {
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

            return publisher;
        };
    }
}
