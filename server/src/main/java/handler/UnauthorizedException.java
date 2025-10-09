package handler;

public class UnauthorizedException extends ResponseException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
