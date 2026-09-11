package com.demo.bookstorebatch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "authors")
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String biography;
    @ManyToOne(fetch = FetchType.LAZY)
    private Publisher publisher;

    /**
     * CSV-only value. The processor resolves it to {@link #publisher} before
     * the entity is written, so it is not part of the authors table.
     */
    @Transient
    private String publisherName;
}
