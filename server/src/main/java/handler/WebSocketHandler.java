package handler;

import chess.ChessMove;
import handler.exception.ResponseException;
import model.AuthData;
import websocket.messages.ServerMessage;

public class WebSocketHandler {

    public AuthData handleAuth(String authtoken, int gameID) throws ResponseException {
        return new AuthData(authtoken, "web_socket_handler_username");
    }

    public ServerMessage connect(String authtoken, int gameID) throws ResponseException {
        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
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
        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
    }
}
