package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.AuthData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessGameClient extends Client {

    public enum JoinType {
        BLACK,
        OBSERVER,
        WHITE

    }

    private final int TILE_PADDING_LENGTH = 1;
    private final String TILE_PADDING = " ".repeat(TILE_PADDING_LENGTH);
    private final String BORDER_BG_COLOR = SET_BG_COLOR_MAGENTA;
    private final String BORDER_TEXT_COLOR = SET_TEXT_COLOR_BLACK;

    private final String WHITE_BG = SET_BG_COLOR_WHITE;
    private final String BLACK_BG = SET_BG_COLOR_BLACK;

    private record Tile(String body, String bgColor, String textColor) {
    }

    private final JoinType joinType;
    private final int gameID;

    private List<List<Tile>> printGrid = new ArrayList<List<Tile>>();

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

    public ChessGameClient(JoinType joinType, AuthData authData, int gameID) throws ClientException {
        this.joinType = joinType;
        this.gameID = gameID;

        // TODO: Update with WebSocket code for phase six. This is a dummy function that pretends to call the server to see if you are authenticated.
        if (Objects.equals(authData.authToken(), "")) {
            throw new ClientException("Bad Authentication");
        }

    }

    @Override
    public void run() {
        System.out.println("THIS IS THE CHESS GAME CLIENT. YOU ARE ON TEAM " + joinType.toString());


        chessBoard = new ChessBoard();
        chessBoard.resetBoard();

        printGrid.add(letters);



        for (int row = 1; row <= 8; row++)
        {
            var tileRow = new ArrayList<Tile>();

            tileRow.add(new Tile(
                    Integer.parseInt(row),

            ));

            for (int col = 1; col <= 8; col++)
            {

            }
        }

        printGrid.add(letters);

        render();
    }

    private void render() {
        for (final var row : printGrid) {
            for (final var tile : row) {
                System.out.printf("%s%s%s%s%s",
                        tile.bgColor,
                        TILE_PADDING,
                        tile.textColor,
                        TILE_PADDING,
                        tile.body);
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
