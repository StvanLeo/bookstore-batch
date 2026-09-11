package com.demo.bookstorebatch.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String isbn;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Author> authors;

    /**
     * CSV-only, semicolon-separated author Names (for example: "Torra;Hennessy;Scott").
     * The processor resolves the Names to {@link #authors} before persistence.
     */
    @Transient
    private String authorNames;
}
