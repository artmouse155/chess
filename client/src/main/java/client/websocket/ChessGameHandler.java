package client.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ClientException;
import client.Handler;
import client.painter.BoardPainter;
import com.google.gson.Gson;
import model.AuthData;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class ChessGameHandler extends Handler {

    private static final String CHESS_POSITION = "[A-H][1-8]";

    private final WebSocketFacade webSocketFacade;
    private final Consumer<String> onWebSocketMessage;
    private ChessGame chessGame;
    private ChessPosition highlightPosition;

    private final String playerHelp = """
            m <source> <destination>  | Make a move
            h <position>              | Highlight available moves
            redraw                    | Redraw the game board
            resign                    | Resign game
            leave                     | Leave game
            echo <message>            | Echo a message to test that WebSocket works
            help                      | See list of commands
            """;

    private final String observerHelp = """
            h <position>    | Highlight available moves
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

        webSocketFacade.sendCommand(UserGameCommand.CommandType.CONNECT);
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
        return switch (joinType) {
            case BLACK, WHITE -> playerHelp;
            case OBSERVER -> observerHelp;
        };
    }


    public String makeMove(String... params) throws ClientException {
        validateArgs(params, "m <source> <destination>\n", CHESS_POSITION, CHESS_POSITION);
        webSocketFacade.makeMove(ChessMove.fromString(params[0], params[1]));
        return "";
    }

    public String highlight(String... params) throws ClientException {
        validateArgs(params, "h <position>\n", CHESS_POSITION);
        highlightPosition = ChessPosition.fromString(params[0]);
        return redraw();
    }

    public String redraw(String... params) {

        if (chessGame == null) {
            return "Please wait.\n";
        }

        Collection<ChessMove> validMoves = (highlightPosition == null) ? Set.of() : chessGame.validMoves(highlightPosition);

        return String.format("%sBoard drawn.\n",
                BoardPainter.displayBoard(
                        chessGame.getBoard(),
                        (joinType == ChessGameClient.JoinType.BLACK),
                        highlightPosition,
                        validMoves
                )
        );
    }

    public String resign(String... params) throws ClientException {
        webSocketFacade.resign();
        return "";
    }

    public String leave(String... params) throws ClientException {
        webSocketFacade.close();
        return "";
    }

    public String echo(String... params) throws ClientException {
        validateArgs(params, "echo <msg>\n", STRING);
        webSocketFacade.sendEcho(params[0]);
        return "Message sent\n";
    }

    private String formatServerLoadGame(ChessGame chessGame) {
        this.chessGame = chessGame;
        return String.format("%s", redraw());
    }

    private String formatServerError(String serverError) {
        return String.format("Error: %s", serverError);
    }

    private String formatServerNotification(String serverNotification) {
        return String.format("INFO: %s", serverNotification);
    }

    public void formatWebSocketResponse(String message) {
        var serializer = new Gson();
        var res = serializer.fromJson(message, ServerMessage.class);
        String output = switch (res.getServerMessageType()) {
            case LOAD_GAME -> formatServerLoadGame(serializer.fromJson(message, LoadGameMessage.class).getChessGame());
            case ERROR -> formatServerError(serializer.fromJson(message, ErrorMessage.class).getErrorMessage());
            case NOTIFICATION -> formatServerNotification(serializer.fromJson(message, NotificationMessage.class).getMessage());
        };
        onWebSocketMessage.accept(output);
    }

}
