package handler;

import chess.ChessMove;
import dataaccess.DataAccessException;
import handler.exception.InternalServerErrorException;
import handler.exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import service.WebSocketService;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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

    public AuthData handleAuth(String authtoken, int gameID) throws ResponseException {
        return new AuthData(authtoken, "web_socket_handler_username");
    }

    public ServerMessage connect(Session session, String username, int gameID) throws ResponseException {
        try {
            var chessGame = wsService.connect(session, username, gameID);
            return new LoadGameMessage(chessGame);
        } catch (DataAccessException | IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public ServerMessage makeMove(ChessMove move) throws ResponseException {
        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    }

    public ServerMessage leave(String authtoken, int gameID) throws ResponseException {
        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    }

    public ServerMessage resign(String authtoken, int gameID) throws ResponseException {
        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    }

    public ServerMessage echo(String message) throws ResponseException {
        return new NotificationMessage(message);
    }
}
