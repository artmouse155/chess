package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import client.websocket.WsEchoClient;
import model.AuthData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

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

    private final JoinType joinType;
    private final int gameID;

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

    public ChessGameClient(JoinType joinType, String authToken, int gameID) throws ClientException {
        this.joinType = joinType;
        this.gameID = gameID;

        try {
            var wsClient = new WsEchoClient(String.format("ws://localhost:8080/unauthGame/%d", gameID), authToken);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a message you want to echo:");
            while (true) {
                wsClient.send(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.printf("Websocket failed. Error:%s%n", e.getMessage());
        }

        // Update with WebSocket code for phase six. This is a dummy function that pretends to call the server to see if you are authenticated.
        if (Objects.equals(authToken, "")) {
            throw new ClientException("Bad Authentication");
        }

    }

    @Override
    public void run() {
        System.out.printf("Welcome! You are on team %s. Blue letters represent black pieces and red letters represent white pieces.%n",
                joinType.toString());

        chessBoard = new ChessBoard();
        chessBoard.resetBoard();

        displayBoard(chessBoard, (joinType == JoinType.BLACK));
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

    }

    @Override
    protected String formatError(ClientException ex) {
        return "";
    }

    @Override
    protected String eval(String input) {
        return "";
    }
}
