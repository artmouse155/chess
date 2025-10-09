package handler;

public class AlreadyTakenException extends ResponseException {
    public AlreadyTakenException(String message) {
        super(message);
    }
}
