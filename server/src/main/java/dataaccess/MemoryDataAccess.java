package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public class MemoryDataAccess implements DataAccess{
    @Override
    public void deleteDB() throws DataAccessException {
        
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public void removeAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public List<GameData> getGameData() throws DataAccessException {
        return List.of();
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void getGame(int gameID) throws DataAccessException {

    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {

    }
}
