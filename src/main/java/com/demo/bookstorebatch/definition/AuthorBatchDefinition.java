package com.demo.bookstorebatch.definition;

import com.demo.bookstorebatch.batch.EntityBatchDefinition;
import com.demo.bookstorebatch.exception.DuplicateResourceException;
import com.demo.bookstorebatch.exception.NotFoundResourceException;
import com.demo.bookstorebatch.exception.RequiredResourceException;
import com.demo.bookstorebatch.model.Author;
import com.demo.bookstorebatch.model.Publisher;
import com.demo.bookstorebatch.repository.AuthorRepository;
import com.demo.bookstorebatch.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorBatchDefinition implements EntityBatchDefinition<Author> {
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;

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
                throw new RequiredResourceException("Author", "Publisher name");
            }

            Publisher publisher = publisherRepository.findByName(publisherName)
                    .orElseThrow(() -> new NotFoundResourceException(
                            "Publisher", publisherName));
            author.setPublisher(publisher);

            Example<Author> authorExample = Example.of(author);

            if (authorRepository.exists(authorExample)) {
               throw new DuplicateResourceException("Author", author.getName());
            }

            return author;
        };
    }
}
