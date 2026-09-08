package com.demo.bookstorebatch.definition;

import com.demo.bookstorebatch.batch.EntityBatchDefinition;
import com.demo.bookstorebatch.model.Author;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class AuthorBatchDefinition implements EntityBatchDefinition<Author> {

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
                "publisher"
        };
    }

    @Override
    public ItemProcessor<Author, Author> processor() {
        return author -> {
            author.setName(author.getName());
            author.setBiography(author.getBiography());

            return author;
        };
    }
}
