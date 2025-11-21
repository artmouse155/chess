package client.websocket;

import client.Client;
import client.ClientException;
import client.Handler;
import model.AuthData;

import java.util.*;

import static ui.EscapeSequences.*;

public class ChessGameClient extends Client {

    public enum JoinType {
        BLACK,
        OBSERVER,
        WHITE

    }

    private static final Collection<String> TERMINAL_STRINGS = Set.of("leave");

    @Override
    protected Collection<String> getTerminalStrings() {
        return TERMINAL_STRINGS;
    }

    private static final String OBSERVER_GAME_NAME_FORMAT = SET_BG_COLOR_YELLOW + SET_TEXT_COLOR_BLACK;
    private static final String WHITE_GAME_NAME_FORMAT = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK;
    private static final String BLACK_GAME_NAME_FORMAT = SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE;


    private final ChessGameHandler chessGameHandler;


    public ChessGameClient(String url, JoinType joinType, String gameName, AuthData authData, int gameID) throws Exception {
        chessGameHandler = new ChessGameHandler(url, joinType, gameName, authData, gameID,
                this::onWebSocketMessage);
    }

    @Override
    protected String getIntroMessage() {
        String joinMessage = switch (chessGameHandler.getJoinType()) {
            case BLACK -> "You are playing with the black pieces.";
            case OBSERVER -> "You are observing this game.";
            case WHITE -> "You are playing with the white pieces.";
        };
        return String.format("""
                        Join game "%s" successful. Welcome, %s!
                        %s
                        Blue letters represent black pieces and red letters represent white pieces.
                        %s
                        """,
                chessGameHandler.getGameName(),
                chessGameHandler.getUserName(),
                joinMessage,
                chessGameHandler.help()
        );
    }

    @Override
    protected void printPrompt() {
        String gameNameFormat = switch (chessGameHandler.getJoinType()) {
            case WHITE -> WHITE_GAME_NAME_FORMAT;
            case BLACK -> BLACK_GAME_NAME_FORMAT;
            case OBSERVER -> OBSERVER_GAME_NAME_FORMAT;

        };
        System.out.printf("%s ♕ 240 Chess ♕ %s %s %s %s %s> ", APP_TITLE_FORMAT, gameNameFormat, chessGameHandler.getGameName(),
                USERNAME_FORMAT, chessGameHandler.getUserName(), RESET_ALL);
    }

    @Override
    protected Handler.TerminalFunction getTerminalCommand(String cmd) {
        return switch (chessGameHandler.getJoinType()) {
            case OBSERVER -> switch (cmd) {
                case "h" -> chessGameHandler::highlight;
                case "redraw" -> chessGameHandler::redraw;
                case "leave" -> chessGameHandler::leave;
                case "echo" -> chessGameHandler::echo;
                case "" -> (String... p) -> "";
                default -> chessGameHandler::help;
            };
            case BLACK, WHITE -> switch (cmd) {
                case "m" -> chessGameHandler::makeMove;
                case "h" -> chessGameHandler::highlight;
                case "redraw" -> chessGameHandler::redraw;
                case "resign" -> chessGameHandler::resign;
                case "leave" -> chessGameHandler::leave;
                case "echo" -> chessGameHandler::echo;
                case "" -> (String... p) -> "";
                default -> chessGameHandler::help;
            };
        };
    }

    private void onWebSocketMessage(String message) {
        System.out.printf("\r%s%n", message);
        printPrompt();
    }
}
