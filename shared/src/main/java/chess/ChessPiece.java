package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    private enum AttackMode {
        ALLOWED,
        ONLY,
        NEVER
    }

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

    private boolean boolPointTest(ChessBoard board, ChessPosition myPosition, int rowOffset, int colOffset, AttackMode attackMode) {

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = piece.getTeamColor();

        ChessPosition endPosition = new ChessPosition(row + rowOffset, col + colOffset);
        if (!board.hasPosition(endPosition)) {
            return false;
        }
        ChessPiece endPiece = board.getPiece(endPosition);
        ChessGame.TeamColor endColor = null;
        if (endPiece != null) {
            {
                endColor = endPiece.getTeamColor();
                if ((myColor == endColor) || (attackMode == AttackMode.NEVER))
                {
                    return false;
                }
            }

        } else if (attackMode == AttackMode.ONLY) {
            return false;
        }
        return true;
    }

    private void probeTest(ChessBoard board, ChessPosition myPosition, int rowVector, int colVector, HashSet<ChessMove> moves, AttackMode attackMode, boolean promote,  int maxIterations)
    {

        for (int i = 1; i <= maxIterations; i++) {
            if (!boolPointTest(board, myPosition, rowVector * i, colVector * i, attackMode))
                return;
            ChessPosition endPosition = new ChessPosition(myPosition.getRow() + rowVector * i,myPosition.getColumn() + colVector * i);
            if (promote) {
                moves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
            } else {
                moves.add(new ChessMove(myPosition, endPosition));
            }
            if (board.getPiece(endPosition) != null)
                break;
        }
    }

    private void probeTest(ChessBoard board, ChessPosition myPosition, int rowVector, int colVector, HashSet<ChessMove> moves)
    {
        probeTest(board, myPosition, rowVector, colVector, moves, AttackMode.ALLOWED, false, 8);
    }

    private void pointTest(ChessBoard board, ChessPosition myPosition, int rowOffset, int colOffset, HashSet<ChessMove> moves, AttackMode attackMode, boolean promote)
    {
        probeTest(board, myPosition, rowOffset, colOffset, moves, attackMode, promote, 1);
    }

    private void pointTest(ChessBoard board, ChessPosition myPosition, int rowOffset, int colOffset, HashSet<ChessMove> moves)
    {
        probeTest(board, myPosition, rowOffset, colOffset, moves, AttackMode.ALLOWED, false, 1);
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
        HashSet<ChessMove> moves = new HashSet<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor myColor = piece.getTeamColor();
        PieceType myType = piece.getPieceType();

        switch (myType) {
            case PieceType.BISHOP:
                probeTest(board, myPosition, 1, 1, moves);
                probeTest(board, myPosition, -1, 1, moves);
                probeTest(board, myPosition, 1, -1, moves);
                probeTest(board, myPosition, -1, -1, moves);
                break;
            case PieceType.KING:
                pointTest(board, myPosition, 1, 0, moves);
                pointTest(board, myPosition, -1, 0, moves);
                pointTest(board, myPosition, 0, 1, moves);
                pointTest(board, myPosition, 0, -1, moves);
                pointTest(board, myPosition, 1, 1, moves);
                pointTest(board, myPosition, -1, 1, moves);
                pointTest(board, myPosition, 1, -1, moves);
                pointTest(board, myPosition, -1, -1, moves);
                break;
            case PieceType.KNIGHT:
                pointTest(board, myPosition, 1, 2, moves);
                pointTest(board, myPosition, 1, -2, moves);
                pointTest(board, myPosition, -1, 2, moves);
                pointTest(board, myPosition, 2, 1, moves);
                pointTest(board, myPosition, 2, -1, moves);
                pointTest(board, myPosition, -2, 1, moves);
                pointTest(board, myPosition, -2, -1, moves);
                break;
            case PieceType.PAWN:
                if (myColor == ChessGame.TeamColor.BLACK) {
                    // Standard movement
                    probeVectors.add(new Vector(-1, 0, AttackMode.NEVER, (row == 7) ? 2 : 1, (row == 2)));

                    // Attack vectors
                    possibleEndPositionVectors.add(new Vector(-1, -1, AttackMode.ONLY, 1, (row == 2)));
                    possibleEndPositionVectors.add(new Vector(-1, 1, AttackMode.ONLY, 1, (row == 2)));

                } else if (myColor == ChessGame.TeamColor.WHITE) {
                    // Standard movement
                    probeVectors.add(new Vector(1, 0, AttackMode.NEVER, (row == 2) ? 2 : 1, (row == 7)));

                    // Attack vectors
                    possibleEndPositionVectors.add(new Vector(1, -1, AttackMode.ONLY, 1, (row == 7)));
                    possibleEndPositionVectors.add(new Vector(1, 1, AttackMode.ONLY, 1, (row == 7)));
                }
                break;
            case PieceType.QUEEN:
                probeVectors.add(new Vector(1, 1));
                probeVectors.add(new Vector(-1, 1));
                probeVectors.add(new Vector(1, -1));
                probeVectors.add(new Vector(-1, -1));
                probeVectors.add(new Vector(0, 1));
                probeVectors.add(new Vector(0, -1));
                probeVectors.add(new Vector(1, 0));
                probeVectors.add(new Vector(-1, 0));
                break;
            case PieceType.ROOK:
                probeVectors.add(new Vector(0, 1));
                probeVectors.add(new Vector(0, -1));
                probeVectors.add(new Vector(1, 0));
                probeVectors.add(new Vector(-1, 0));
                break;
        }

        return moves;
    }

    @Override
    public String toString() {
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            return switch (type) {
                case PieceType.BISHOP -> "B";
                case PieceType.KING -> "K";
                case PieceType.KNIGHT -> "N";
                case PieceType.PAWN -> "P";
                case PieceType.QUEEN -> "Q";
                case PieceType.ROOK -> "R";
            };
        } else if (pieceColor == ChessGame.TeamColor.BLACK) {
            return switch (type) {
                case PieceType.BISHOP -> "b";
                case PieceType.KING -> "k";
                case PieceType.KNIGHT -> "n";
                case PieceType.PAWN -> "p";
                case PieceType.QUEEN -> "q";
                case PieceType.ROOK -> "r";
            };
        } else return "?";
    }
}
