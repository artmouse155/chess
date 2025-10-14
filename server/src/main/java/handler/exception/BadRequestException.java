package handler.exception;

public class BadRequestException extends ResponseException {
    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public int getHTTPCode() {
        return 400;
    }

    @Override
    public String getHTTPResponseString() {
        return "bad request";
    }
}
