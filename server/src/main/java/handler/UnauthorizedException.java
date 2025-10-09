package handler;

public class UnauthorizedException extends ResponseException {
    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public int getHTTPCode() {
        return 401;
    }

    @Override
    public String getHTTPResponseString() {
        return "unauthorized";
    }
}
