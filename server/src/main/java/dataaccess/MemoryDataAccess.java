package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashSet;
import java.util.Set;

public class MemoryDataAccess implements DataAccess {

    Set<UserData> userDataSet;
    Set<AuthData> authDataSet;
    Set<GameData> gameDataSet;

    public MemoryDataAccess() {
        userDataSet = new HashSet<>();
        authDataSet = new HashSet<>();
        gameDataSet = new HashSet<>();

        debugPrint("User Data Set Init", userDataSet);
        debugPrint("Auth Data Set Init", authDataSet);
        debugPrint("Game Data Set Init", gameDataSet);
    }

    private void debugPrint(String message, Set s) {
        System.out.println(message);
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
        debugPrint("User Data Set Clear", userDataSet);
        debugPrint("Auth Data Set Clear", authDataSet);
        debugPrint("Game Data Set Clear", gameDataSet);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var filteredStream = userDataSet.stream().filter(userData -> userData.username().equals(username));
        var first = filteredStream.findFirst();
        if (first.isEmpty()) {
            throw new UserNotFoundException("Attempted to get user that did not exist.");
        }
        return first.get();
    }

    @Override
    public boolean hasUser(String username) throws DataAccessException {
        try {
            getUser(username);
        } catch (UserNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        userDataSet.add(userData);
        debugPrint("User Data Set Create", userDataSet);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public boolean hasAuth(String authToken) throws DataAccessException {
        try {
            getAuth(authToken);
        } catch (AuthNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        authDataSet.add(authData);
        debugPrint("Auth Data Set Create", authDataSet);
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        authDataSet.removeIf(authData -> authData.authToken().equals(authToken));
        debugPrint("Auth Data Set Remove " + authToken, authDataSet);
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
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        debugPrint("Game Data Set Create", gameDataSet);
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        debugPrint("User Data Set Update", gameDataSet);
    }
}
