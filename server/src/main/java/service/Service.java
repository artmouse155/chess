package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import handler.exception.*;
import model.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Service {

    private int gameCount = 0;
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

    public Map<String, Set<? extends Record>> getDB() throws DataAccessException {
        return dataAccess.getDB();
    }

    public EmptyResponse deleteDB() throws ResponseException {
        try {
            dataAccess.deleteDB();
            gameCount = 0;
            return new EmptyResponse();
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

    public EmptyResponse logout(String authToken) throws ResponseException {
        try {
            dataAccess.removeAuth(authToken);
            return new EmptyResponse();
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public GamesList getGames() throws ResponseException {
        try {
            return new GamesList(dataAccess.getGameDataSet().stream().map(GameData::stripped).collect(Collectors.toSet()));
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public CreateGameResponse createGame(String gameName) throws ResponseException {
        try {
            int gameID = gameCount + 1;
            dataAccess.addGame(new GameData(gameID, null, null, gameName, new ChessGame()));
            gameCount++;
            return new CreateGameResponse(gameID);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public EmptyResponse joinGame(String username, String playerColor, int gameID) throws ResponseException {
        try {
            GameData game = dataAccess.getGame(gameID);
            if (playerColor.equals("WHITE")) {
                if (game.whiteUsername() != null) {
                    throw new AlreadyTakenException("The color white has already been taken.");
                }
                game = game.setWhiteUsername(username);
            } else if (playerColor.equals("BLACK")) {
                if (game.blackUsername() != null) {
                    throw new AlreadyTakenException("The color black has already been taken.");
                }
                game = game.setBlackUsername(username);
            } else {
                throw new BadRequestException("Unrecognized color requested.");
            }
            dataAccess.updateGame(gameID, game);
            return new EmptyResponse();
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}