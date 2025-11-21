package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import model.GameConnectionPool;
import model.GameParticipant;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

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

    public ChessGame connect(String username, int gameID) throws DataAccessException {

        if (!gameConnectionPoolMap.containsKey(gameID)) {
            gameConnectionPoolMap.put(gameID, new GameConnectionPool());
        }
        var gameData = dataAccess.getGame(gameID);
        var pool = gameConnectionPoolMap.get(gameID);
        String connectMessage = "";
        if (gameData.whiteUsername().equals(username) && pool.whiteUsername() == null) {
            pool.setWhitePlayer(new GameParticipant(username));
            connectMessage = String.format("%s joined as white player", username);
        } else if (gameData.blackUsername().equals(username) && pool.blackUsername() == null) {
            pool.setBlackPlayer(new GameParticipant(username));
            connectMessage = String.format("%s joined as black player", username);
        } else {
            pool.addObserver(new GameParticipant(username));
            connectMessage = String.format("%s joined as observer", username);
        }

        pool.sendMessage(new LoadGameMessage(gameData.game()), ONLY_SELF, username);
        pool.sendMessage(new NotificationMessage(connectMessage), ONLY_OTHERS, username);

        return gameData.game();
    }
}
