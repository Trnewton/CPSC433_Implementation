package Exceptions;

import java.lang.Exception;

public class InvalidInputException extends Exception {

    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }
}
