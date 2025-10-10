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

        debugPrint(userDataSet);
        debugPrint(authDataSet);
        debugPrint(gameDataSet);
    }

    private void debugPrint(Set s) {
        System.out.println("Size = " + s.size());
        for (var item : s) {
            System.out.println(item.toString());
        }
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
        debugPrint(userDataSet);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        authDataSet.add(authData);
        debugPrint(authDataSet);
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        authDataSet.removeIf(authData -> authData.authToken().equals(authToken));
        debugPrint(authDataSet);
    }


    @Override
    public Set<GameData> getGameDataSet() throws DataAccessException {
        return gameDataSet;
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        debugPrint(gameDataSet);
    }

    @Override
    public void getGame(int gameID) throws DataAccessException {

    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        debugPrint(gameDataSet);
    }
}
