package org.vescm.zooapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AnimalLimitExceededException extends Exception {
    public AnimalLimitExceededException(String specie) {
        super("Can't have more animals of specie " + specie + ".");
    }
}
