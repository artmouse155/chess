package service;

import chess.ChessGame;
import chess.ChessMove;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import handler.exception.BadRequestException;
import handler.exception.InternalServerErrorException;
import handler.exception.ResponseException;
import handler.exception.UnauthorizedException;
import model.AuthData;
import model.GameConnectionPool;
import model.GameParticipant;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static model.GameConnectionPool.BroadcastType.ONLY_OTHERS;
import static model.GameConnectionPool.BroadcastType.ONLY_SELF;

public class WebSocketService {

    private final DataAccess dataAccess;
    private Map<Integer, GameConnectionPool> gameConnectionPoolMap = new HashMap<>();


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

        if (!gameConnectionPoolMap.containsKey(gameID)) {
            gameConnectionPoolMap.put(gameID, new GameConnectionPool());
        }
        var gameData = dataAccess.getGame(gameID);
        var pool = gameConnectionPoolMap.get(gameID);
        String connectMessage = "";
        if (gameData.whiteUsername().equals(username) && pool.whiteUsername() == null) {
            pool.setWhitePlayer(new GameParticipant(session, username));
            connectMessage = String.format("%s joined as white player", username);
        } else if (gameData.blackUsername().equals(username) && pool.blackUsername() == null) {
            pool.setBlackPlayer(new GameParticipant(session, username));
            connectMessage = String.format("%s joined as black player", username);
        } else {
            pool.addObserver(new GameParticipant(session, username));
            connectMessage = String.format("%s joined as observer", username);
        }

        pool.sendMessage(new LoadGameMessage(gameData.game()), ONLY_SELF, username);
        pool.sendMessage(new NotificationMessage(connectMessage), ONLY_OTHERS, username);
    }

    public void makeMove(String username, ChessMove move) {
    }

    public void leave(String username, int gameID) {
    }

    public void resign(String username, int gameID) {
    }

    public void echo(String message) {
    }
}
