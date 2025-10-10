package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import handler.AlreadyTakenException;
import handler.InternalServerErrorException;
import handler.ResponseException;
import handler.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Map;
import java.util.Set;

public class Service {

    private final DataAccess dataAccess;

    public Service() {
        dataAccess = new MemoryDataAccess();
    }

    public AuthData authenticate(String authToken) throws ResponseException {
        try {
            if (!dataAccess.hasAuth(authToken)) {
                throw new UnauthorizedException("Invalid authToken.");
            }
            return dataAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public Map<String, String> deleteDB() throws ResponseException {
        try {
            dataAccess.deleteDB();
            return Map.of();
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public AuthData register(UserData userData) throws ResponseException {
        try {
            var username = userData.username();

            if (dataAccess.hasUser(username)) {
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
        try {


            if (!dataAccess.hasUser(username)) {
                throw new UnauthorizedException("User does not exist.");
            }

            var userData = dataAccess.getUser(username);
            if (!password.equals(userData.password())) {
                throw new UnauthorizedException("Password is incorrect.");
            }

            var authToken = generateAuthToken();
            var authData = new AuthData(authToken, username);
            dataAccess.createAuth(authData);
            return authData;
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public Map<String, String> logout(String authToken) throws ResponseException {
        try {
            dataAccess.removeAuth(authToken);
            return Map.of();
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public Set<GameData> getGames() throws ResponseException {
        try {
            return dataAccess.getGameDataSet();
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public Map<String, Number> createGame(String gameName) throws ResponseException {
        try {
            int gameID = gameName.hashCode();
            dataAccess.addGame(new GameData(gameID, null, null, gameName, new ChessGame()));
            return Map.of("gameID", gameID);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public Map<String, String> joinGame(String playerColor, int gameID) throws ResponseException {
        try {
            GameData game = dataAccess.getGame(gameID);
            return Map.of();
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public String generateAuthToken() {
        return "Service auth token";
    }

}