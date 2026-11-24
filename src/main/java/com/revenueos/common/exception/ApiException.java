package com.revenueos.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base for domain errors that map to a specific HTTP status. Throwing these from
 * services keeps controllers free of error-handling plumbing.
 */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
