package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Map;
import java.util.Set;

public interface DataAccess {

    // For debug purposes
    public Map<String, Set<? extends Record>> getDB() throws DataAccessException;

    public void deleteDB() throws DataAccessException;

    public UserData getUser(String username) throws DataAccessException;

    public boolean hasUser(String username) throws DataAccessException;

    public void createUser(UserData userData) throws DataAccessException;

    public AuthData getAuth(String authToken) throws DataAccessException;

    public boolean hasAuth(String authToken) throws DataAccessException;

    public void createAuth(AuthData authData) throws DataAccessException;

    public void removeAuth(String authToken) throws DataAccessException;

    public Set<GameData> getGameDataSet() throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;

    public boolean hasGame(int gameID) throws DataAccessException;

    public void addGame(GameData gameData) throws DataAccessException;

    public void updateGame(int gameID, GameData gameData) throws DataAccessException;

    public void removeGame(int gameID) throws DataAccessException;

}
