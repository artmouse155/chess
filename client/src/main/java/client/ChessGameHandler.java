package client;

import client.websocket.WebSocketFacade;
import model.AuthData;
import websocket.commands.UserGameCommand;

import java.util.function.Consumer;

public class ChessGameHandler extends Handler {

    private final WebSocketFacade webSocketFacade;
    private final Consumer<String> onWebSocketMessage;

    private final String playerHelp = """
            m <move>        | Make a move
            h <location>    | Highlight available moves
            redraw          | Redraw the game board
            resign          | Resign game
            leave           | Leave game
            echo <message>  | Echo a message to test that WebSocket works
            help            | See list of commands
            """;

    private final String observerHelp = """
            redraw          | Redraw the game board
            leave           | Leave game
            echo <message>  | Echo a message to test that WebSocket works
            help            | See list of commands
            """;

    private final ChessGameClient.JoinType joinType;
    private final String gameName;
    private final String username;

    public ChessGameHandler(String url, ChessGameClient.JoinType joinType, String gameName, AuthData authData, int gameID,
                            Consumer<String> onWebSocketMessage) throws Exception {
        this.joinType = joinType;
        this.gameName = gameName;
        this.username = authData.username();
        this.onWebSocketMessage = onWebSocketMessage;
        webSocketFacade = new WebSocketFacade(url, authData.authToken(), gameID, this::formatWebSocketResponse);
    }

    public ChessGameClient.JoinType getJoinType() {
        return joinType;
    }

    public String getGameName() {
        return gameName;
    }

    public String getUserName() {
        return username;
    }

    @Override
    public String help(String... params) {
        return "Observer help\n";
    }

    public String playerHelp(String... params) {
        return switch (joinType) {
            case BLACK, WHITE -> playerHelp;
            case OBSERVER -> observerHelp;
        };
    }

    public String makeMove(String... params) {
        return "makeMove\n";
    }

    public String highlight(String... params) {
        return "highlight\n";
    }

    public String redraw(String... params) {
        return "redraw\n";
    }

    public String resign(String... params) {
        return "resign\n";
    }

    public String leave(String... params) throws ClientException {
        webSocketFacade.close();
        return "leave";
    }

    public String echo(String... params) throws ClientException {
        validateArgs(params, "echo <msg>\n", STRING);
        webSocketFacade.sendCommand(UserGameCommand.CommandType.ECHO, params[0]);
        return "Message sent\n";
    }

    public void formatWebSocketResponse(String message) {
        onWebSocketMessage.accept(message);
    }

}
