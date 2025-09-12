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
    private final boolean canBeEnPassanted;

    private enum MoveMode {
        ATTACK_ALLOWED,
        ATTACK_ONLY,
        ATTACK_NEVER,
        ATTACK_EN_PASSANT,
        CASTLE
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, boolean canBeEnPassanted)
    {
        this.pieceColor = pieceColor;
        this.type = type;
        this.canBeEnPassanted = canBeEnPassanted;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.canBeEnPassanted = false;
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

    private boolean boolPointTest(ChessBoard board, ChessPosition myPosition, int rowOffset, int colOffset, MoveMode moveMode) {

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = piece.getTeamColor();

        ChessPosition endPosition = new ChessPosition(row + rowOffset, col + colOffset);
        if (!board.hasPosition(endPosition)) {
            return false;
        }
        ChessPiece endPiece = board.getPiece(endPosition);
        if (moveMode == MoveMode.ATTACK_EN_PASSANT)
        {
            endPiece = board.getPiece(new ChessPosition((myColor== ChessGame.TeamColor.WHITE)?5:4,col));
        }
        ChessGame.TeamColor endColor = null;
        if (endPiece != null) {
            {
                endColor = endPiece.getTeamColor();
                if ((myColor == endColor) || (moveMode == MoveMode.ATTACK_NEVER))
                {
                    return false;
                }

                if (moveMode == MoveMode.ATTACK_EN_PASSANT && !endPiece.getCanBeEnPassanted())
                {
                    return false;
                }
            }

        } else if (moveMode == MoveMode.ATTACK_ONLY) {
            return false;
        }
        return true;
    }

    private void probeTest(
            ChessBoard board,
            ChessPosition myPosition,
            HashSet<ChessMove> moves,
            int rowVector,
            int colVector,
            MoveMode moveMode,
            boolean promote,
            int maxIterations
    )
    {

        for (int i = 1; i <= maxIterations; i++) {
            if (!boolPointTest(board, myPosition, rowVector * i, colVector * i, moveMode)) {
                return;
            }
            ChessPosition endPosition = new ChessPosition(myPosition.getRow() + rowVector * i,myPosition.getColumn() + colVector * i);
            if (promote) {
                moves.add(new ChessMove(myPosition, endPosition, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, endPosition, PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, endPosition, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, endPosition, PieceType.ROOK));
            } else {
                moves.add(new ChessMove(myPosition, endPosition));
            }
            if (board.getPiece(endPosition) != null) {
                break;
            }
        }
    }

    private void probeTest(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int rowVector, int colVector)
    {
        probeTest(board, myPosition, moves, rowVector, colVector, MoveMode.ATTACK_ALLOWED, false, 8);
    }

    private void pointTest(
            ChessBoard board,
            ChessPosition myPosition,
            HashSet<ChessMove> moves,
            int rowOffset,
            int colOffset,
            MoveMode moveMode,
            boolean promote)
    {
        probeTest(board, myPosition, moves, rowOffset, colOffset, moveMode, promote, 1);
    }

    private void pointTest(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int rowOffset, int colOffset)
    {
        probeTest(board, myPosition, moves, rowOffset, colOffset, MoveMode.ATTACK_ALLOWED, false, 1);
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

        int row = myPosition.getRow();
        ChessGame.TeamColor myColor = piece.getTeamColor();
        PieceType myType = piece.getPieceType();

        HashSet<ChessMove> moves = new HashSet<>();

        switch (myType) {
            case PieceType.BISHOP:
                probeTest(board, myPosition, moves, 1, 1);
                probeTest(board, myPosition, moves, -1, 1);
                probeTest(board, myPosition, moves, 1, -1);
                probeTest(board, myPosition, moves, -1, -1);
                break;
            case PieceType.KING:
                pointTest(board, myPosition, moves, 1, 0);
                pointTest(board, myPosition, moves, -1, 0);
                pointTest(board, myPosition, moves, 0, 1);
                pointTest(board, myPosition, moves, 0, -1);
                pointTest(board, myPosition, moves, 1, 1);
                pointTest(board, myPosition, moves, -1, 1);
                pointTest(board, myPosition, moves, 1, -1);
                pointTest(board, myPosition, moves, -1, -1);
                break;
            case PieceType.KNIGHT:
                pointTest(board, myPosition, moves, 1, 2);
                pointTest(board, myPosition, moves, 1, -2);
                pointTest(board, myPosition, moves, -1, 2);
                pointTest(board, myPosition, moves, -1, -2);
                pointTest(board, myPosition, moves, 2, 1);
                pointTest(board, myPosition, moves, 2, -1);
                pointTest(board, myPosition, moves, -2, 1);
                pointTest(board, myPosition, moves, -2, -1);
                break;
            case PieceType.PAWN:
                if (myColor == ChessGame.TeamColor.BLACK) {
                    // Standard movement
                    probeTest(board, myPosition, moves, -1, 0, MoveMode.ATTACK_NEVER, (row == 2), (row == 7) ? 2 : 1);

                    // Attack vectors
                    pointTest(board, myPosition, moves, -1, -1, MoveMode.ATTACK_ONLY, (row == 2));
                    pointTest(board, myPosition, moves, -1, 1, MoveMode.ATTACK_ONLY, (row == 2));

                    // En Passant Perhaps
                    pointTest(board, myPosition, moves, -1, -1, MoveMode.ATTACK_EN_PASSANT, false);
                    pointTest(board, myPosition, moves, -1, 1, MoveMode.ATTACK_EN_PASSANT, false);
                } else if (myColor == ChessGame.TeamColor.WHITE) {
                    // Standard movement
                    probeTest(board, myPosition, moves, 1, 0, MoveMode.ATTACK_NEVER, (row == 7), (row == 2) ? 2 : 1);

                    // Attack vectors
                    pointTest(board, myPosition, moves, 1, -1, MoveMode.ATTACK_ONLY, (row == 7));
                    pointTest(board, myPosition, moves, 1, 1, MoveMode.ATTACK_ONLY, (row == 7));

                    // En Passant Perhaps
                    pointTest(board, myPosition, moves, 1, -1, MoveMode.ATTACK_EN_PASSANT, false);
                    pointTest(board, myPosition, moves, 1, 1, MoveMode.ATTACK_EN_PASSANT, false);
                }
                break;
            case PieceType.QUEEN:
                probeTest(board, myPosition, moves, 1, 1);
                probeTest(board, myPosition, moves, -1, 1);
                probeTest(board, myPosition, moves, 1, -1);
                probeTest(board, myPosition, moves, -1, -1);
                probeTest(board, myPosition, moves, 0, 1);
                probeTest(board, myPosition, moves, 0, -1);
                probeTest(board, myPosition, moves, 1, 0);
                probeTest(board, myPosition, moves, -1, 0);
                break;
            case PieceType.ROOK:
                probeTest(board, myPosition, moves, 0, 1);
                probeTest(board, myPosition, moves, 0, -1);
                probeTest(board, myPosition, moves, 1, 0);
                probeTest(board, myPosition, moves, -1, 0);
                break;
        }

        return moves;
    }

    @Override
    public String toString() {
        if (canBeEnPassanted) return "⭐";
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
        } else {
            return "?";
        }
    }

    public boolean getCanBeEnPassanted()
    {
        return canBeEnPassanted;
    }
}
