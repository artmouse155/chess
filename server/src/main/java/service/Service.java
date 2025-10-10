package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import handler.AlreadyTakenException;
import handler.InternalServerErrorException;
import handler.ResponseException;
import handler.UnauthorizedException;
import model.AuthData;
import model.UserData;

import java.util.List;
import java.util.Map;

public class Service {

    private final DataAccess dataAccess;

    public Service() {
        dataAccess = new MemoryDataAccess();
    }

    public Map<String, String> deleteDB() throws ResponseException {
        return Map.of();
    }

    public AuthData register(UserData userData) throws ResponseException {
        try {
            var username = userData.username();

            if (dataAccess.getUser(username) != null) {
                throw new AlreadyTakenException("Failed to register username \"" + username + "\". Already taken.");
            }
            dataAccess.createUser(userData);
            var authToken = generateAuthToken();
            var authData = new AuthData(authToken, username);
            dataAccess.createAuth(authData);
            return authData;
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public AuthData login(String username, String password) throws ResponseException {

        var userData = dataAccess.getUser(username);

        if (userData == null || !password.equals(userData.password()))
        {
            throw new UnauthorizedException("Credentials don't match.");
        }
        var authToken = generateAuthToken();
        var authData = new AuthData(authToken, username);
        dataAccess.createAuth(authData);
        return authData;
    }

    public Map<String, String> logout(String authToken) throws ResponseException {
        dataAccess.removeAuth(authToken);
        return Map.of();
    }

    public Map<String, List<Map<String, Object>>> getGames() {
        return Map.of();
    }

    public Map<String, Number> createGame(String gameName) {
        return Map.of();
    }

    public Map<String, String> joinGame(String playerColor, int gameID) {
        return Map.of();
    }

    public String generateAuthToken()
    {
        return "Service auth token";
    }

}