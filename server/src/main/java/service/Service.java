package service;

import dataaccess.DataAccess;
import handler.AlreadyTakenException;

import java.util.List;
import java.util.Map;

public class Service {

    private final DataAccess dataAccess;

    public Service() {
        dataAccess = new DataAccess();
    }

    public Map<String, String> deleteDB() {
        return Map.of();
    }

    public Map<String, String> register(String username, String password, String email) throws AlreadyTakenException {

        var userData = dataAccess.getUser(username);
        if (userData != null)
        {
            throw new AlreadyTakenException("Failed to register username \"" + username + "\". Already taken.");
        }
        var authToken = generateAuthToken();
        dataAccess.createAuth(username, authToken);
        return Map.of("username", username, "authToken", authToken);
    }

    public Map<String, String> login(String username, String password) {
        return Map.of();
    }

    public Map<String, String> logout(String authToken) {
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