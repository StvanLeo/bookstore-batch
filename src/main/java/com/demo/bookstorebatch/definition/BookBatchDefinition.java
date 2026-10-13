package com.demo.bookstorebatch.definition;

import com.demo.bookstorebatch.batch.EntityBatchDefinition;
import com.demo.bookstorebatch.exception.DuplicateResourceException;
import com.demo.bookstorebatch.exception.NotFoundResourceException;
import com.demo.bookstorebatch.exception.RequiredResourceException;
import com.demo.bookstorebatch.model.Author;
import com.demo.bookstorebatch.model.Book;
import com.demo.bookstorebatch.repository.AuthorRepository;
import com.demo.bookstorebatch.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class BookBatchDefinition implements EntityBatchDefinition<Book> {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

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
            List<String> authorNames = parseAuthorNames(book.getAuthorNames());
            List<Author> authors = authorRepository.findDistinctByNameIn(authorNames);
            if (authors.size() != authorNames.size()) {
                throw new NotFoundResourceException(
                        "Authors ", book.getAuthorNames());
            }
            book.setAuthors(authors);

            Example<Book> bookExample = Example.of(book);

            if (bookRepository.exists(bookExample)) {
                throw new DuplicateResourceException("Book", book.getTitle());
            }

            return book;
        };
    }

    private List<String> parseAuthorNames(String authorNames) {
        if (authorNames == null || authorNames.isBlank()) {
            throw new RequiredResourceException("Book", "Author names");
        }

        return Stream.of(authorNames.split(";"))
                .map(String::trim)
                .toList();
    }
}
