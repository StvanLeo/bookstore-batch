package com.demo.bookstorebatch.repository;

import com.demo.bookstorebatch.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
