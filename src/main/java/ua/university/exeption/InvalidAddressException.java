package ua.university.exeption;

public class InvalidAddressException extends ValidationException {
    public InvalidAddressException(String message) {
        super(message);
    }
}