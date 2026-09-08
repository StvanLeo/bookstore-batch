package com.demo.bookstorebatch.definition;

import com.demo.bookstorebatch.batch.EntityBatchDefinition;
import com.demo.bookstorebatch.model.Book;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BookBatchDefinition implements EntityBatchDefinition<Book> {
    @Override
    public String name() {
        return "book";
    }

    @Override
    public Class<Book> entityType() {
        return Book.class;
    }

    @Override
    public String[] csvColumns() {
        return new String[]{
                "title",
                "isbn",
                "authors"
        };
    }

    @Override
    public ItemProcessor<Book, Book> processor() {
        return book -> {
            book.setTitle(book.getTitle());
            book.setIsbn(book.getIsbn());
            book.setAuthors(book.getAuthors());

            return book;
        };
    }
}
