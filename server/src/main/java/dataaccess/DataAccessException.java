package dataaccess;

import handler.InternalServerErrorException;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends InternalServerErrorException {
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }
}
