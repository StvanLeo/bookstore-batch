package com.demo.bookstorebatch.repository;

import com.demo.bookstorebatch.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findDistinctByNameIn(List<String> name);
}
