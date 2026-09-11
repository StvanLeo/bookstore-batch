package com.demo.bookstorebatch.definition;

import com.demo.bookstorebatch.batch.EntityBatchDefinition;
import com.demo.bookstorebatch.model.Author;
import com.demo.bookstorebatch.model.Book;
import com.demo.bookstorebatch.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class BookBatchDefinition implements EntityBatchDefinition<Book> {
    private final AuthorRepository authorRepository;
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
                "authorNames"
        };
    }

    @Override
    public ItemProcessor<Book, Book> processor() {
        return book -> {
            book.setTitle(book.getTitle());
            book.setIsbn(book.getIsbn());
            List<String> authorIds = parseAuthorIds(book.getAuthorNames());
            List<Author> authors = authorRepository.findDistinctByNameIn(authorIds);
            if (authors.size() != authorIds.size()) {
                throw new IllegalArgumentException(
                        "One or more authors were not found: " + book.getAuthorNames());
            }
            book.setAuthors(authors);

            return book;
        };
    }

    private List<String> parseAuthorIds(String authorIds) {
        if (authorIds == null || authorIds.isBlank()) {
            throw new IllegalArgumentException("Book authorNames is required");
        }

        return Stream.of(authorIds.split(";"))
                .map(String::trim)
                .toList();
    }
}
