package com.demo.bookstorebatch.exception;

public abstract class BookStoreBatchException extends RuntimeException {
    public BookStoreBatchException(String message) {
        super(message);
    }
}
