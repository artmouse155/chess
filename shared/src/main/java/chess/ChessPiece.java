package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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

        if (piece.getPieceType() == PieceType.BISHOP) {
            
            // Calculate Bottom Left [1,1] to Top Right [8,8]. Code will ONLY WORK with an 8*8 board
            for (int r = 1; r <= board.squares.length; r++) {
                if (r != row)
                {

                    ChessPosition endPosition = new ChessPosition(r,r);
                    if (board.hasPosition(endPosition))
                    {
                        moves.add(new ChessMove(myPosition, endPosition, null));
                    }
                }
            }
            
            // Calculate Bottom Right [1,8] to Top Left [8,1]
            for (int c = 1; c <= board.squares[0].length; c++) {
                
            }

        }
        return moves;
    }
}
