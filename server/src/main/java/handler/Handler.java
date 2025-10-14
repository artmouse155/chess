package handler;

import dataaccess.DataAccessException;
import handler.exception.BadRequestException;
import handler.exception.ResponseException;
import model.AuthData;
import model.GameDataStripped;
import model.UserData;
import service.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Handler {

    private final Service service;

    public Handler() {
        service = new Service();
    }

    public AuthData handleAuth(String authToken) throws ResponseException {
        return service.authenticate(authToken);
    }

    public Map<String, Set<? extends Record>> handleGetDB() throws DataAccessException {
        return service.getDB();
    }

    public Map<String, String> handleDeleteDB() throws ResponseException {
        return service.deleteDB();
    }

    public AuthData handleRegister(UserData userData) throws ResponseException {
        if (
                userData.password() == null ||
                        userData.username() == null ||
                        userData.email() == null ||
                        userData.password().isEmpty() ||
                        userData.username().isEmpty() ||
                        userData.email().isEmpty()
        ) {
            throw new BadRequestException("One or more fields is invalid.");
        }
        return service.register(userData);
    }

    public AuthData handleLogin(String username, String password) throws ResponseException {
        if (username == null || password == null) {
            throw new BadRequestException("One or more fields is invalid.");
        }
        return service.login(username, password);
    }

    public Map<String, String> handleLogout(String authToken) throws ResponseException {

        return service.logout(authToken);
    }

    public Map<String, Set<GameDataStripped>> handleGetGames() throws ResponseException {
        return service.getGames();
    }

    public Map<String, Number> handleCreateGame(String gameName) throws ResponseException {
        if (gameName == null) {
            throw new BadRequestException("One or more fields is invalid.");
        }
        return service.createGame(gameName);
    }

    public Map<String, String> handleJoinGame(String username, String playerColor, int gameID) throws ResponseException {
        if (gameID < 0 || (!Objects.equals(playerColor, "WHITE") && !Objects.equals(playerColor, "BLACK"))) {
            throw new BadRequestException("One or more fields is invalid.");
        }
        return service.joinGame(username, playerColor, gameID);
    }
}