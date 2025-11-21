package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ClientException;
import client.Handler;
import client.painter.BoardPainter;
import client.repl.ChessGameREPL;
import client.repl.PromoteREPL;
import client.repl.ResignREPL;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

import static ui.EscapeSequences.*;

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

    private final ChessGameREPL.JoinType joinType;
    private final String gameName;
    private final String username;

    public ChessGameHandler(String url, ChessGameREPL.JoinType joinType, String gameName, String authToken, String username, int gameID,
                            Consumer<String> onWebSocketMessage) throws Exception {
        this.joinType = joinType;
        this.gameName = gameName;
        this.username = username;
        this.onWebSocketMessage = onWebSocketMessage;
        webSocketFacade = new WebSocketFacade(url, authToken, gameID, this::formatWebSocketResponse);

        webSocketFacade.sendCommand(UserGameCommand.CommandType.CONNECT);
    }

    public ChessGameREPL.JoinType getJoinType() {
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
        var move = ChessMove.fromString(params[0], params[1]);
        var piece = chessGame.getBoard().getPiece(move.getStartPosition());
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.pieceMoves(
                chessGame.getBoard(), move.getStartPosition()
        ).contains(
                new ChessMove(
                        move.getStartPosition(),
                        move.getEndPosition(),
                        ChessPiece.PieceType.QUEEN
                ))) {

            String result = (new PromoteREPL().run());
            switch (result) {
                case "q" -> {
                    move = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN);
                }
                case "b" -> {
                    move = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.BISHOP);
                }
                case "k" -> {
                    move = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.KNIGHT);
                }
                case "r" -> {
                    move = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.ROOK);
                }
                case "c" -> {
                    return "Promotion aborted.\n";
                }
            }
        }
        webSocketFacade.makeMove(move);
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
                        (joinType == ChessGameREPL.JoinType.BLACK),
                        highlightPosition,
                        validMoves
                )
        );
    }

    public String resign(String... params) throws ClientException {
        boolean doResign = (new ResignREPL().run()).equals("y");
        if (doResign) {
            webSocketFacade.resign();
            return "";
        } else {
            return "Resign aborted.\n";
        }
    }

    public String leave(String... params) throws ClientException {
        webSocketFacade.close();
        return "leave";
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
        return String.format("%sError:%s %s", SET_TEXT_COLOR_RED, RESET_TEXT_COLOR, serverError);
    }

    private String formatServerNotification(String serverNotification) {
        return String.format("%s%s \uD83D\uDEC8 %s%s %s", SET_BG_COLOR_WHITE, SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR, RESET_BG_COLOR,
                serverNotification);
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
