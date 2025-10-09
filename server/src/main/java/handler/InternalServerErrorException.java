package handler;

public class InternalServerErrorException extends ResponseException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
