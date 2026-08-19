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
}
