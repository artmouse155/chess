package handler;

public class BadRequestException extends ResponseException {
    public BadRequestException(String message) {
        super(message);
    }
}
