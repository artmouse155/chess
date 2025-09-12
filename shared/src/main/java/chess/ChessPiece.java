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
    private final boolean haveMoved;

    private enum MoveMode {
        ATTACK_ALLOWED,
        ATTACK_ONLY,
        ATTACK_NEVER,
        ATTACK_EN_PASSANT,
        CASTLE
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, boolean haveMoved) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.haveMoved = haveMoved;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.haveMoved = false;
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
        } else {
            return "?";
        }
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

    public boolean getHaveNeverMoved() {
        return !haveMoved;
    }

    private boolean boolPointTest(ChessBoard board, ChessPosition myPosition, int rowOffset, int colOffset, MoveMode moveMode) {

        // My Piece & Position
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = piece.getTeamColor();
        ChessPosition endPosition = new ChessPosition(row + rowOffset, col + colOffset);

        // End Piece and Position
        ChessPosition endPiecePosition;
        if (moveMode == MoveMode.ATTACK_EN_PASSANT) {
            endPiecePosition = new ChessPosition((myColor == ChessGame.TeamColor.WHITE) ? 5 : 4, endPosition.getColumn());
        } else {
            endPiecePosition = endPosition;
        }
        if (!board.hasPosition(endPosition)) {
            return false;
        }
        ChessPiece endPiece = board.getPiece(endPiecePosition);
        ChessGame.TeamColor endColor = (endPiece == null) ? null : endPiece.getTeamColor();

        // Test 1: Cannot land on own piece
        if ((myColor == endColor)) {
            return false;
        }

        // Test 2: Check each case
        switch (moveMode) {
            case ATTACK_ONLY -> { return (endColor != null);
            }
            case ATTACK_NEVER, CASTLE -> {  return (endColor == null);
            }
            case ATTACK_EN_PASSANT -> {
                ChessPosition lastPawnMoveTwicePosition = board.getLastPawnMoveTwicePosition();
                if (lastPawnMoveTwicePosition != null) {
                    return endPiecePosition.equals(lastPawnMoveTwicePosition);
                }
                return false;
            }
            case null, default -> { return true; }
        }
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
                if (moveMode == MoveMode.ATTACK_EN_PASSANT)
                {
                    moves.add(new ChessMove(myPosition, endPosition, null, ChessMove.SpecialMove.EN_PASSANT));
                } else if (moveMode == MoveMode.CASTLE) {
                    moves.add(new ChessMove(myPosition, endPosition, null, ChessMove.SpecialMove.CASTLE));
                } else {
                    moves.add(new ChessMove(myPosition, endPosition));
                }
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

        HashSet<ChessMove> moves = new HashSet<>();

        switch (board.getPiece(myPosition).getPieceType()) {
            case PieceType.BISHOP -> {getBishopMoves(board, myPosition, moves);}
            case PieceType.KING -> {getKingMoves(board, myPosition, moves);}
            case PieceType.KNIGHT -> {getKnightMoves(board, myPosition, moves);}
            case PieceType.PAWN -> {getPawnMoves(board, myPosition, moves);}
            case PieceType.QUEEN -> {getQueenMoves(board, myPosition, moves);}
            case PieceType.ROOK -> {getRookMoves(board, myPosition, moves);}
        }

        return moves;
    }

    private void getBishopMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        probeTest(board, myPosition, moves, 1, 1);
        probeTest(board, myPosition, moves, -1, 1);
        probeTest(board, myPosition, moves, 1, -1);
        probeTest(board, myPosition, moves, -1, -1);
    }

    private void getKingMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        ChessPiece piece = board.getPiece(myPosition);
        int row = myPosition.getRow();

        pointTest(board, myPosition, moves, 1, 0);
        pointTest(board, myPosition, moves, -1, 0);
        pointTest(board, myPosition, moves, 0, 1);
        pointTest(board, myPosition, moves, 0, -1);
        pointTest(board, myPosition, moves, 1, 1);
        pointTest(board, myPosition, moves, -1, 1);
        pointTest(board, myPosition, moves, 1, -1);
        pointTest(board, myPosition, moves, -1, -1);

        // Castling
        if (piece.getHaveNeverMoved()) {
            // Left Rook
            ChessPiece rookLeft = board.getPiece(new ChessPosition(row, 1));
            if ((rookLeft != null) && rookLeft.getHaveNeverMoved()) {
                ChessPosition[] intermediatePoints = {
                        new ChessPosition(row, 2),
                        new ChessPosition(row, 3),
                        new ChessPosition(row, 4)
                };
                if (board.positionsEmpty(intermediatePoints)) {
                    pointTest(board, myPosition, moves, 0, -2, MoveMode.CASTLE, false);
                }
            }
            // Right Rook
            ChessPiece rookRight = board.getPiece(new ChessPosition(row, 8));
            if ((rookRight != null) && rookRight.getHaveNeverMoved()) {
                ChessPosition[] intermediatePoints = {
                        new ChessPosition(row, 6),
                        new ChessPosition(row, 7)
                };
                if (board.positionsEmpty(intermediatePoints)) {
                    pointTest(board, myPosition, moves, 0, 2, MoveMode.CASTLE, false);
                }
            }
        }
    }

    private void getKnightMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        pointTest(board, myPosition, moves, 1, 2);
        pointTest(board, myPosition, moves, 1, -2);
        pointTest(board, myPosition, moves, -1, 2);
        pointTest(board, myPosition, moves, -1, -2);
        pointTest(board, myPosition, moves, 2, 1);
        pointTest(board, myPosition, moves, 2, -1);
        pointTest(board, myPosition, moves, -2, 1);
        pointTest(board, myPosition, moves, -2, -1);
    }

    private void getPawnMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = piece.getTeamColor();
        int row = myPosition.getRow();

        if (myColor == ChessGame.TeamColor.BLACK) {
                // Standard movement
                probeTest(board, myPosition, moves, -1, 0, MoveMode.ATTACK_NEVER, (row == 2), (row == 7) ? 2 : 1);

                // Attack
                pointTest(board, myPosition, moves, -1, -1, MoveMode.ATTACK_ONLY, (row == 2));
                pointTest(board, myPosition, moves, -1, 1, MoveMode.ATTACK_ONLY, (row == 2));

                // En Passant
                pointTest(board, myPosition, moves, -1, -1, MoveMode.ATTACK_EN_PASSANT, false);
                pointTest(board, myPosition, moves, -1, 1, MoveMode.ATTACK_EN_PASSANT, false);
            } else if (myColor == ChessGame.TeamColor.WHITE) {
                // Standard movement
                probeTest(board, myPosition, moves, 1, 0, MoveMode.ATTACK_NEVER, (row == 7), (row == 2) ? 2 : 1);

                // Attack
                pointTest(board, myPosition, moves, 1, -1, MoveMode.ATTACK_ONLY, (row == 7));
                pointTest(board, myPosition, moves, 1, 1, MoveMode.ATTACK_ONLY, (row == 7));

                // En Passant
                pointTest(board, myPosition, moves, 1, -1, MoveMode.ATTACK_EN_PASSANT, false);
                pointTest(board, myPosition, moves, 1, 1, MoveMode.ATTACK_EN_PASSANT, false);
            }
    }

    private void getQueenMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        probeTest(board, myPosition, moves, 1, 1);
        probeTest(board, myPosition, moves, -1, 1);
        probeTest(board, myPosition, moves, 1, -1);
        probeTest(board, myPosition, moves, -1, -1);
        probeTest(board, myPosition, moves, 0, 1);
        probeTest(board, myPosition, moves, 0, -1);
        probeTest(board, myPosition, moves, 1, 0);
        probeTest(board, myPosition, moves, -1, 0);
    }

    private void getRookMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        probeTest(board, myPosition, moves, 0, 1);
        probeTest(board, myPosition, moves, 0, -1);
        probeTest(board, myPosition, moves, 1, 0);
        probeTest(board, myPosition, moves, -1, 0);
    }
}
