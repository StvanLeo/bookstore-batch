package com.demo.bookstorebatch.exception;

public class RequiredResourceException extends BookStoreBatchException {

    public RequiredResourceException(String resource, String resourceName) {
        super(resource + " " + resourceName + " is required");
    }
}
