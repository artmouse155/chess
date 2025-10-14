package handler.exception;

public abstract class ResponseException extends Exception {
    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(String message, Throwable ex) {
        super(message, ex);
    }

    public abstract int getHTTPCode();

    public abstract String getHTTPResponseString();

    public String toJson() {
        return String.format("{ \"message\": \"Error: %s \"}", getHTTPResponseString());
    }
}
