package bnorbert.onlineshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException(final String message) {
        super(message);
    }

}