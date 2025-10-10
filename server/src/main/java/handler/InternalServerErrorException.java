package handler;

import dataaccess.DataAccessException;

public class InternalServerErrorException extends ResponseException {
    public InternalServerErrorException(String message) {
        super(message);
    }
    public InternalServerErrorException(String message, Throwable ex) {
        super(message, ex);
    }

    public InternalServerErrorException(DataAccessException e) {
        super(e.getMessage());
    }

    @Override
    public int getHTTPCode() {
        return 500;
    }

    @Override
    public String getHTTPResponseString() {
        return getMessage();
    }
}
