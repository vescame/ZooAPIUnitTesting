package org.vescm.zooapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AnimalNotFoundException extends Exception {
    public AnimalNotFoundException(String description) {
        super("Animal " + description + " doesn't exist.");
    }
}
