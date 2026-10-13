package com.demo.bookstorebatch.exception;

public class DuplicateResourceException extends BookStoreBatchException{
    public DuplicateResourceException(String resource, String resourceName) {
        super(resource + " already exists: " + resourceName);
    }
}
