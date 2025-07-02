package uz.pdp.exceptions;

public class InvalidCartException extends RuntimeException {
    public InvalidCartException(String message) {
        super(message);
    }
}
