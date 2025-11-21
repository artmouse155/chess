package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;

public class WebSocketService {

    private final DataAccess dataAccess;


    public WebSocketService() throws DataAccessException {
        dataAccess = new SQLDataAccess();
    }

    public ChessGame connect(String username, int gameID) throws DataAccessException {
        return dataAccess.getGame(gameID).game();
    }
}
