package dataaccess;

public class GameNotFoundException extends DataAccessException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
