package client;

import chess.ChessBoard;
import chess.ChessPosition;
import model.AuthData;

import java.util.*;

import static ui.EscapeSequences.*;

public class ChessGameClient extends Client {

    public enum JoinType {
        BLACK,
        OBSERVER,
        WHITE

    }

    private static final int TILE_PADDING_LENGTH = 1;
    private static final String TILE_PADDING = " ".repeat(TILE_PADDING_LENGTH);
    private static final String BORDER_BG_COLOR = SET_BG_COLOR_MAGENTA;
    private static final String BORDER_TEXT_COLOR = SET_TEXT_COLOR_BLACK;

    private static final String WHITE_BG = SET_BG_COLOR_WHITE;
    private static final String BLACK_BG = SET_BG_COLOR_BLACK;

    private static final String WHITE_PIECE_COLOR = SET_TEXT_COLOR_RED;
    private static final String BLACK_PIECE_COLOR = SET_TEXT_COLOR_BLUE;


    private record Tile(String body, String bgColor, String textColor) {
    }

    private final ChessGameHandler chessGameHandler;

    private ChessBoard chessBoard;

    private final List<Tile> letters = List.of(
            new Tile(" ", BORDER_BG_COLOR, BORDER_TEXT_COLOR),
            new Tile("a", BORDER_BG_COLOR, BORDER_TEXT_COLOR),
            new Tile("b", BORDER_BG_COLOR, BORDER_TEXT_COLOR),
            new Tile("c", BORDER_BG_COLOR, BORDER_TEXT_COLOR),
            new Tile("d", BORDER_BG_COLOR, BORDER_TEXT_COLOR),
            new Tile("e", BORDER_BG_COLOR, BORDER_TEXT_COLOR),
            new Tile("f", BORDER_BG_COLOR, BORDER_TEXT_COLOR),
            new Tile("g", BORDER_BG_COLOR, BORDER_TEXT_COLOR),
            new Tile("h", BORDER_BG_COLOR, BORDER_TEXT_COLOR),
            new Tile(" ", BORDER_BG_COLOR, BORDER_TEXT_COLOR)
    );

    public ChessGameClient(JoinType joinType, String gameName, AuthData authData, int gameID) throws Exception {
        chessGameHandler = new ChessGameHandler("ws://localhost:8080/unauthGame", joinType, gameName, authData, gameID,
                this::onWebSocketMessage);
    }

    @Override
    public void run() {
        System.out.printf("Welcome! You are on team %s. Blue letters represent black pieces and red letters represent white pieces.%n",
                chessGameHandler.getJoinType().toString());

        chessBoard = new ChessBoard();
        chessBoard.resetBoard();

        displayBoard(chessBoard, (chessGameHandler.getJoinType() == JoinType.BLACK));

        System.out.print(chessGameHandler.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            result = eval(line);
            if (result.equals("leave")) {
                return;
            }

            System.out.print(result);
        }
    }

    private void displayBoard(ChessBoard board, boolean reversed) {
        List<List<Tile>> printGrid = new ArrayList<List<Tile>>();


        printGrid.add(reversed ? letters.reversed() : letters);


        var bgColor = WHITE_BG;

        for (int row = 1; row <= 8; row++) {
            List<Tile> tileRow = new ArrayList<>();

            final var tileBorder = new Tile(
                    String.valueOf(9 - row),
                    BORDER_BG_COLOR,
                    BORDER_TEXT_COLOR
            );

            tileRow.add(tileBorder);

            for (int col = 1; col <= 8; col++) {

                // Changed draw direction, so we need to invert row and col
                var piecePosition = new ChessPosition(9 - row, col);

                var piece = chessBoard.getPiece(piecePosition);

                if (piece != null) {

                    String pieceColor = switch (piece.getTeamColor()) {
                        case WHITE -> WHITE_PIECE_COLOR;
                        case BLACK -> BLACK_PIECE_COLOR;
                    };

                    String displayString = switch (piece.getPieceType()) {
                        case KING -> "K";
                        case QUEEN -> "Q";
                        case BISHOP -> "B";
                        case KNIGHT -> "N";
                        case ROOK -> "R";
                        case PAWN -> "P";
                    };

                    tileRow.add(new Tile(
                            displayString,
                            bgColor,
                            pieceColor
                    ));
                } else {
                    tileRow.add(new Tile(
                            " ",
                            bgColor,
                            RESET_TEXT_COLOR
                    ));
                }
                bgColor = (bgColor.equals(WHITE_BG)) ? BLACK_BG : WHITE_BG;

            }

            bgColor = (bgColor.equals(WHITE_BG)) ? BLACK_BG : WHITE_BG;

            tileRow.add(tileBorder);

            if (reversed) {
                tileRow = tileRow.reversed();
            }

            printGrid.add(tileRow);
        }

        printGrid.add(reversed ? letters.reversed() : letters);

        if (reversed) {
            printGrid = printGrid.reversed();
        }

        renderPrintGrid(printGrid);
    }

    private void renderPrintGrid(List<List<Tile>> printGrid) {
        for (final var row : printGrid) {
            for (final var tile : row) {
                System.out.printf("%s%s%s%s%s",
                        tile.bgColor,
                        tile.textColor,
                        TILE_PADDING,
                        tile.body,
                        TILE_PADDING);
            }
            System.out.printf("%s%s%n", RESET_BG_COLOR, RESET_TEXT_COLOR);
        }
    }

    @Override
    protected void printPrompt() {
        System.out.print("Chess Game > ");
        String prompt = switch (chessGameHandler.getJoinType()) {
            case WHITE, BLACK ->
                    String.format("%s ♕ 240 Chess ♕ %s %s %s> ", APP_TITLE_FORMAT, USERNAME_FORMAT, chessGameHandler.getGameName(), RESET_ALL);
            case OBSERVER -> String.format("%s ♕ 240 Chess ♕ %s> ", APP_TITLE_FORMAT, RESET_ALL);
        };
        System.out.print(prompt);
    }

    @Override
    protected String eval(String input) {
        try {
            String[] tokens = input.split(" +");
            String cmd;
            String[] params;
            if ((tokens.length > 0)) {
                cmd = tokens[0].toLowerCase();
                params = Arrays.copyOfRange(tokens, 1, tokens.length);
            } else {
                cmd = "";
                params = new String[]{};
            }
            Handler.TerminalFunction terminalFunction = switch (chessGameHandler.getJoinType()) {
                case OBSERVER -> switch (cmd) {
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
            return terminalFunction.evaluate(params);
        } catch (ClientException ex) {
            return formatError(ex);
        }
    }

    private void onWebSocketMessage(String message) {
        System.out.printf("%n%s%n", message);
        printPrompt();
    }
}
