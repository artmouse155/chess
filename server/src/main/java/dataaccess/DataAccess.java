package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;
import java.util.Set;

public interface DataAccess {

    public void deleteDB() throws DataAccessException;

    public UserData getUser(String username) throws DataAccessException;

    public void createUser(UserData userData) throws DataAccessException;

    public void createAuth(AuthData authData) throws DataAccessException;

    public void removeAuth(String authToken) throws DataAccessException;

    public Set<GameData> getGameDataSet() throws DataAccessException;

    public GameData getGame(String gameName) throws DataAccessException;

    public void addGame(GameData gameData) throws DataAccessException;

    public void getGame(int gameID) throws DataAccessException;

    public void updateGame(int gameID, GameData gameData) throws DataAccessException;

}
