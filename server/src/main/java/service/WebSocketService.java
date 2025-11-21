package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import handler.exception.BadRequestException;
import handler.exception.InternalServerErrorException;
import handler.exception.ResponseException;
import handler.exception.UnauthorizedException;
import model.AuthData;
import model.GameConnectionPool;
import model.GameData;
import model.GameParticipant;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static model.GameConnectionPool.BroadcastType.*;

public class WebSocketService {

    private record ConnectionID(String username, int gameID) {
    }

    private final DataAccess dataAccess;
    private final Map<Integer, GameConnectionPool> gameConnectionPoolMap = new HashMap<>();
    private final Map<Session, ConnectionID> connections = new HashMap<>();

    public WebSocketService() throws DataAccessException {
        dataAccess = new SQLDataAccess();
    }

    public AuthData verifyAuth(String authToken, int gameID) throws ResponseException {
        try {
            if (!dataAccess.hasAuth(authToken)) {
                throw new UnauthorizedException("Invalid authToken.");
            }
            if (!dataAccess.hasGame(gameID)) {
                throw new BadRequestException("Invalid Game ID.");
            }
            return dataAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(e);
        }
    }

    public void connect(Session session, String username, int gameID) throws DataAccessException, IOException {

        connections.put(session, new ConnectionID(username, gameID));

        if (!gameConnectionPoolMap.containsKey(gameID)) {
            gameConnectionPoolMap.put(gameID, new GameConnectionPool());
        }
        var gameData = dataAccess.getGame(gameID);
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        var pool = gameConnectionPoolMap.get(gameID);
        String connectMessage;
        if (whiteUsername != null && whiteUsername.equals(username) && pool.whiteUsername() == null) {
            pool.setWhitePlayer(new GameParticipant(session, username));
            connectMessage = String.format("%s joined as white player", username);
        } else if (blackUsername != null && blackUsername.equals(username) && pool.blackUsername() == null) {
            pool.setBlackPlayer(new GameParticipant(session, username));
            connectMessage = String.format("%s joined as black player", username);
        } else {
            pool.addObserver(new GameParticipant(session, username));
            connectMessage = String.format("%s joined as observer", username);
        }

        System.out.printf("📖 [WS] %s connected%n", username);

        pool.sendMessage(new LoadGameMessage(gameData.game()), ONLY_SELF, username);
        pool.sendMessage(new NotificationMessage(connectMessage), ONLY_OTHERS, username);
    }

    public void makeMove(String username, int gameID, ChessMove move) throws DataAccessException, IOException, ResponseException {
        if (!gameConnectionPoolMap.containsKey(gameID)) {
            throw new BadRequestException("Invalid Game ID.");
        }
        var gameData = dataAccess.getGame(gameID);

        if (gameData.gameState() == GameData.GameState.RESIGNED) {
            throw new BadRequestException("Game has been resigned.");
        }

        var pool = gameConnectionPoolMap.get(gameID);
        try {
            var teamTurn = gameData.game().getTeamTurn();
            switch (teamTurn) {
                case WHITE -> {
                    if (!username.equals(gameData.whiteUsername())) {
                        throw new UnauthorizedException("Not authorized to make that move.");
                    }
                }
                case BLACK -> {
                    if (!username.equals(gameData.blackUsername())) {
                        throw new UnauthorizedException("Not authorized to make that move.");
                    }
                }
            }

            gameData.game().makeMove(move);
            dataAccess.updateGame(gameID, gameData);

            pool.sendMessage(new LoadGameMessage(gameData.game()), ALL, username);
            pool.sendMessage(new NotificationMessage(move.toString()), ONLY_OTHERS, username);

            var game = gameData.game();
            teamTurn = game.getTeamTurn();
            String currentUsername = (teamTurn == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

            if (game.isInCheck(teamTurn)) {
                pool.sendMessage(new NotificationMessage(String.format("%s (%s) is in check", currentUsername, teamTurn)), ALL, username);
            } else if (game.isInCheckmate(teamTurn)) {
                pool.sendMessage(new NotificationMessage(String.format("%s (%s) is in checkmate", currentUsername, teamTurn)), ALL, username);
            } else if (game.isInStalemate(teamTurn)) {
                pool.sendMessage(new NotificationMessage(String.format("%s (%s) is in stalemate", currentUsername, teamTurn)), ALL, username);
            }

        } catch (InvalidMoveException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void leave(String username, int gameID) throws DataAccessException, IOException, BadRequestException {
        if (!gameConnectionPoolMap.containsKey(gameID)) {
            throw new BadRequestException("Invalid Game ID.");
        }
        var gameData = dataAccess.getGame(gameID);
        var pool = gameConnectionPoolMap.get(gameID);

        if (username.equals(pool.whiteUsername())) {
            pool.removeWhitePlayer();
            dataAccess.updateGame(gameID, gameData.setWhiteUsername(null));
        } else if (username.equals(pool.blackUsername())) {
            pool.removeBlackPlayer();
            dataAccess.updateGame(gameID, gameData.setBlackUsername(null));
        } else {
            pool.removeObserver(username);
        }

        System.out.printf("📘 [WS] %s disconnected%n", username);
        pool.sendMessage(new NotificationMessage(String.format("%s left the game", username)), ONLY_OTHERS, username);
    }

    public void resign(String username, int gameID) throws DataAccessException, IOException, ResponseException {
        if (!gameConnectionPoolMap.containsKey(gameID)) {
            throw new BadRequestException("Invalid Game ID.");
        }
        var gameData = dataAccess.getGame(gameID);
        var pool = gameConnectionPoolMap.get(gameID);
        if (!(username.equals(pool.whiteUsername()) || username.equals(pool.blackUsername()))) {
            throw new UnauthorizedException("Not authorized to resign.");
        }

        if (gameData.gameState() == GameData.GameState.RESIGNED) {
            throw new BadRequestException("Game already resigned.");
        }

        dataAccess.updateGame(gameID, gameData.resign());

        pool.sendMessage(new NotificationMessage(String.format("%s resigned.", username)), ALL, username);
    }

    public void echo(String message) throws IOException {
        for (final var pool : gameConnectionPoolMap.values()) {
            pool.sendMessage(new NotificationMessage(String.format("Echo: %s", message)), ALL, null);
        }
    }

    public void disconnect(Session session) throws BadRequestException, IOException, DataAccessException {
        if (connections.containsKey(session)) {
            var connectionID = connections.get(session);
            leave(connectionID.username(), connectionID.gameID());
        }
    }
}
