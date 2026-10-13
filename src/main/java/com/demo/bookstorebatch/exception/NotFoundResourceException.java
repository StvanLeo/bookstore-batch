package com.demo.bookstorebatch.exception;

public class NotFoundResourceException extends BookStoreBatchException {
    public NotFoundResourceException(String resource, String resourceName) {
        super(resource + " not found: " + resourceName);
    }
}
