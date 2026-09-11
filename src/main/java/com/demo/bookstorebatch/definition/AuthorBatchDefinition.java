package com.demo.bookstorebatch.definition;

import com.demo.bookstorebatch.batch.EntityBatchDefinition;
import com.demo.bookstorebatch.model.Author;
import com.demo.bookstorebatch.model.Publisher;
import com.demo.bookstorebatch.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorBatchDefinition implements EntityBatchDefinition<Author> {
    private final PublisherRepository publisherRepository;

    @Override
    public String name() {
        return "author";
    }

    @Override
    public Class<Author> entityType() {
        return Author.class;
    }

    @Override
    public String[] csvColumns() {
        return new String[] {
                "name",
                "biography",
                "publisherName"
        };
    }

    @Override
    public ItemProcessor<Author, Author> processor() {
        return author -> {
            author.setName(author.getName());
            author.setBiography(author.getBiography());
            String publisherName = author.getPublisherName();
            if (publisherName == null) {
                throw new IllegalArgumentException("Author publisherName is required");
            }

            Publisher publisher = publisherRepository.findByName(publisherName)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Publisher not found: " + publisherName));
            author.setPublisher(publisher);

            return author;
        };
    }
}
