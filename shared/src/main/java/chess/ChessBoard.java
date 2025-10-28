package chess;

import com.google.gson.Gson;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares = new ChessPiece[8][8];

    private ChessPosition lastPawnMoveTwicePosition;


    public ChessBoard() {
    }

    public ChessBoard(ChessPiece[][] squares) {
        this.squares = squares;
    }

    public ChessBoard(ChessPiece[][] squares, ChessPosition lastPawnMoveTwicePosition) {
        this.squares = squares;
        this.lastPawnMoveTwicePosition = lastPawnMoveTwicePosition;
    }

    public static ChessBoard fromString(String boardJSON) {
        var serializer = new Gson();
        var req = serializer.fromJson(boardJSON, Map.class);
        String lJSON = req.get("lastPawnMoveTwicePosition").toString();
        var _lastPawnMoveTwicePosition = ChessPosition.fromString(lJSON);
        String squaresJSON = req.get("squares").toString();
        // TODO: IMPLEMENT
        var _squares = new ChessPiece[8][8];
        return new ChessBoard(_squares, _lastPawnMoveTwicePosition);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public void removePiece(ChessPosition position) {
        addPiece(position, null);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Clear board: Unimplemented

        // WHITE
        // Non-Pawns
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        // Pawns
        addPiece(new ChessPosition(2, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

        // BLACK
        // Non-Pawns
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        // Pawns
        addPiece(new ChessPosition(7, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
    }

    /**
     *
     * @param position The position on the board being tested
     * @return True if the piece is actually on the board, false if otherwise
     */
    public boolean hasPosition(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return (row >= 1 && row <= squares.length) && (col >= 1 && col <= squares[0].length);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        String out = "";

        for (ChessPiece[] square : squares) {
            String row = "|";
            for (int c = 0; c < squares[0].length; c++) {
                row += ((square[c] != null) ? square[c].toString() : " ") + "|";
            }
            out = row + "\n" + out;
        }

        return new Gson().toJson(Map.of(
                "lastPawnMoveTwicePosition", lastPawnMoveTwicePosition,
                "squares", out
        ));
    }

    public Collection<ChessPosition> getAllPositions(ChessGame.TeamColor color) {
        HashSet<ChessPosition> positions = new HashSet<>();
        for (int row = 1; row <= squares.length; row++) {
            for (int col = 1; col <= squares[0].length; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = getPiece(position);
                if (piece != null && piece.getTeamColor() == color) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor color) {
        for (int row = 1; row <= squares.length; row++) {
            for (int col = 1; col <= squares[0].length; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = getPiece(position);
                if (
                        piece != null
                                && piece.getTeamColor() == color
                                && piece.getPieceType() == ChessPiece.PieceType.KING
                ) {
                    return position;
                }
            }
        }
        return null;
    }

    public ChessBoard copyAndForceMove(ChessMove move) {

        ChessPiece[][] copySquares = new ChessPiece[squares.length][squares[0].length];

        // Copy data to new board
        for (int row = 0; row < squares.length; row++) {
            for (int col = 0; col < squares[0].length; col++) {
                copySquares[row][col] = squares[row][col];
            }
        }

        ChessBoard copyBoard = new ChessBoard(copySquares);

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ChessPiece piece = copyBoard.getPiece(startPosition);
        copyBoard.addPiece(endPosition, piece);
        copyBoard.addPiece(startPosition, null);

        return copyBoard;
    }

    public ChessPosition getLastPawnMoveTwicePosition() {
        return lastPawnMoveTwicePosition;
    }

    public void setLastPawnMoveTwicePosition(ChessPosition lastPawnMoveTwicePosition) {
        this.lastPawnMoveTwicePosition = lastPawnMoveTwicePosition;
    }

    public boolean positionsEmpty(ChessPosition[] positions) {
        for (ChessPosition position : positions) {
            if (getPiece(position) != null) {
                return false;
            }
        }
        return true;
    }
}
