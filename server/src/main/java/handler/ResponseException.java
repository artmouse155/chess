package handler;

public abstract class ResponseException extends RuntimeException {
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
