package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Map;
import java.util.Set;

public class SQLDataAccess implements DataAccess {

    Connection getConnection() throws DataAccessException {
        final String user = "";
        final String password = "";
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306", user, password);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Map<String, Set<? extends Record>> getDB() throws DataAccessException {
        return Map.of();
    }

    @Override
    public void deleteDB() throws DataAccessException {
        try (var conn = getConnection()) {
            // Do something...
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public boolean hasUser(String username) throws DataAccessException {
        return false;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public boolean hasAuth(String authToken) throws DataAccessException {
        return false;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {

    }

    @Override
    public Set<GameData> getGameDataSet() throws DataAccessException {
        return Set.of();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public boolean hasGame(int gameID) throws DataAccessException {
        return false;
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {

    }

    @Override
    public void removeGame(int gameID) throws DataAccessException {

    }
}
