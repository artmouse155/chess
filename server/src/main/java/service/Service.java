package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import handler.AlreadyTakenException;
import handler.InternalServerErrorException;
import handler.ResponseException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

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

    public AuthData register(UserData userData) throws ResponseException {

        var username = userData.username();
        UserData dataAccessResponse;
        try {
            dataAccessResponse = dataAccess.getUser(username);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e.getMessage());
        }


        if (dataAccessResponse != null)
        {
            throw new AlreadyTakenException("Failed to register username \"" + username + "\". Already taken.");
        }
        dataAccess.createUser(userData);
        var authToken = generateAuthToken();
        var authData = new AuthData(authToken, username);
        dataAccess.createAuth(authData);
        return authData;
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