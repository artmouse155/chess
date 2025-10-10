package dataaccess;

public class AuthNotFoundException extends DataAccessException {
    public AuthNotFoundException(String message) {
        super(message);
    }
}
