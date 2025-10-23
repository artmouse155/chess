package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import handler.exception.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

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

    // For debug purposes
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
            var encryptedPassword = encrypt(userData.password());
            var encryptedUserData = new UserData(
                    userData.username(),
                    encryptedPassword,
                    userData.email()
            );
            dataAccess.createUser(encryptedUserData);
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
            if (!checkPassword(password, userData.password())) {
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

    public EmptyResponse joinGame(AuthData authData, String playerColor, int gameID) throws ResponseException {
        try {
            if (!dataAccess.hasGame(gameID)) {
                throw new BadRequestException("Game does not exist.");
            }

            if (!dataAccess.hasAuth(authData.authToken())) {
                throw new UnauthorizedException("Bad Authorization.");
            }

            // Username can't be wrong because the username isn't supplied by the client
            // it comes from the database during the authentication middleware
            final String username = authData.username();

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

    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

}