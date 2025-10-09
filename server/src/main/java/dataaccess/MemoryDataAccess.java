package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemoryDataAccess implements DataAccess{

    Set<UserData> userDataSet;
    Set<AuthData> authDataSet;
    Set<GameData> gameDataSet;

    public MemoryDataAccess() {
        userDataSet = new HashSet<>();
        authDataSet = new HashSet<>();
        gameDataSet = new HashSet<>();
    }

    @Override
    public void deleteDB() throws DataAccessException {
        userDataSet.clear();
        authDataSet.clear();
        gameDataSet.clear();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var filteredStream = userDataSet.stream().filter(userData -> userData.username().equals(username));
        var first = filteredStream.findFirst();
        return first.orElse(null);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        userDataSet.add(userData);

        System.out.println("UserData set: (Size = " + userDataSet.size() + ")");
        for (var item : userDataSet) {
            System.out.println(item);
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        authDataSet.add(authData);
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
