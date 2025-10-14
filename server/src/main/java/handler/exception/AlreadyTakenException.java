package handler.exception;

public class AlreadyTakenException extends ResponseException {
    public AlreadyTakenException(String message) {
        super(message);
    }

    @Override
    public int getHTTPCode() {
        return 403;
    }

    @Override
    public String getHTTPResponseString() {
        return "already taken";
    }
}
