package handler;

import dataaccess.DataAccessException;
import handler.exception.BadRequestException;
import handler.exception.ResponseException;
import model.*;
import service.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Handler {

    private final Service service;

    public Handler(boolean doSQL) {
        service = new Service(doSQL);
    }

    public AuthData handleAuth(String authToken) throws ResponseException {
        return service.authenticate(authToken);
    }

    // For debug purposes
    public Map<String, Set<? extends Record>> handleGetDB() throws DataAccessException {
        return service.getDB();
    }

    public EmptyResponse handleDeleteDB() throws ResponseException {
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

    public AuthData handleLogin(LoginRequest loginRequest) throws ResponseException {
        if (loginRequest.username() == null ||
                loginRequest.password() == null ||
                loginRequest.username().isEmpty() ||
                loginRequest.password().isEmpty()
        ) {
            throw new BadRequestException("One or more fields is invalid.");
        }
        return service.login(loginRequest);
    }

    public EmptyResponse handleLogout(String authToken) throws ResponseException {
        handleAuth(authToken);
        return service.logout(authToken);
    }

    public GamesSet handleGetGames() throws ResponseException {
        return service.getGames();
    }

    public CreateGameResponse handleCreateGame(String gameName) throws ResponseException {
        if (gameName == null || gameName.isEmpty()) {
            throw new BadRequestException("One or more fields is invalid.");
        }
        return service.createGame(gameName);
    }

    public EmptyResponse handleJoinGame(AuthData authData, String playerColor, int gameID) throws ResponseException {
        final String username = authData.username();

        if (gameID < 0 ||
                (!Objects.equals(playerColor, "WHITE") && !Objects.equals(playerColor, "BLACK")) ||
                username == null ||
                username.isEmpty()
        ) {
            throw new BadRequestException("One or more fields is invalid.");
        }
        return service.joinGame(authData, playerColor, gameID);
    }
}