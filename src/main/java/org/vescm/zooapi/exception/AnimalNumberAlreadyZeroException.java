package org.vescm.zooapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AnimalNumberAlreadyZeroException extends Exception {
    public AnimalNumberAlreadyZeroException() {
        super("Can't negative your animals number.");
    }
}
