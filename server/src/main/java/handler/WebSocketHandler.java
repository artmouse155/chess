package handler;

import chess.ChessMove;
import dataaccess.DataAccessException;
import handler.exception.InternalServerErrorException;
import handler.exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import service.WebSocketService;

import java.io.IOException;

public class WebSocketHandler {

    private final WebSocketService wsService;

    public WebSocketHandler() {
        try {
            wsService = new WebSocketService();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthData handleAuth(String authToken, int gameID) throws ResponseException {
        return wsService.verifyAuth(authToken, gameID);
    }

    public void connect(Session session, String username, int gameID) throws ResponseException {
        try {
            wsService.connect(session, username, gameID);
        } catch (DataAccessException | IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public void makeMove(String username, ChessMove move) throws ResponseException {
        try {
            wsService.makeMove(username, move);
        } catch (DataAccessException | IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public void leave(String username, int gameID) throws ResponseException {
        try {
            wsService.leave(username, gameID);
        } catch (DataAccessException | IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public void resign(String username, int gameID) throws ResponseException {
        try {
            wsService.resign(username, gameID);
        } catch (DataAccessException | IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public void echo(String message) throws ResponseException {
        try {
            wsService.echo(message);
        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
