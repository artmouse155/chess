package client.painter;

import chess.ChessBoard;
import chess.ChessPosition;
import client.websocket.ChessGameClient;

import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class BoardPainter {

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

    private static final List<Tile> LETTERS = List.of(
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

    public static void displayBoard(ChessBoard board, boolean reversed) {
        List<List<Tile>> printGrid = new ArrayList<List<Tile>>();


        printGrid.add(reversed ? LETTERS.reversed() : LETTERS);


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

                var piece = board.getPiece(piecePosition);

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

        printGrid.add(reversed ? LETTERS.reversed() : LETTERS);

        if (reversed) {
            printGrid = printGrid.reversed();
        }

        renderPrintGrid(printGrid);
    }

    private static void renderPrintGrid(List<List<Tile>> printGrid) {
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


}
