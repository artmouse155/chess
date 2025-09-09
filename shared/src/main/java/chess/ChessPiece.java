package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = piece.getTeamColor();

        if (piece.getPieceType() == PieceType.BISHOP) {

            int probeRow = row;
            int probeCol = col;
            int probeVectorRow = 1;
            int probeVectorCol = 1;
            ChessPosition endPosition = new ChessPosition(probeRow, probeCol);
            while (board.hasPosition(endPosition)) {
                probeRow += probeVectorRow;
                probeCol += probeVectorCol;
                endPosition = new ChessPosition(probeRow, probeCol);
                if (!board.hasPosition(endPosition))
                    break;
                ChessPiece endPiece = board.getPiece(endPosition);
                if (endPiece == null)
                    moves.add(new ChessMove(myPosition, endPosition));
                else {
                    ChessGame.TeamColor endColor = endPiece.getTeamColor();
                    if (endColor != color)
                        moves.add(new ChessMove(myPosition, endPosition));
                    break;
                }
            }

//            boolean newMoveFound = true;
//            int i = 0;
//            while (newMoveFound)
//            {
//                i++;
//                newMoveFound = false;
//                ChessPosition[] possibleEndPositions = {myPosition.offsetBy(i,i),myPosition.offsetBy(-i,i),myPosition.offsetBy(i,-i),myPosition.offsetBy(-i,-i)};
//                for (ChessPosition endPosition : possibleEndPositions)
//                {
//                    if (board.hasPosition(endPosition) && board.getPiece(endPosition).getTeamColor() != color) {
//                        newMoveFound = true;
//                        moves.add(new ChessMove(myPosition, endPosition));
//                    }
//                }
//            };

        }
        return moves;
    }
}
