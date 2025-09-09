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
        HashSet<ChessMove> moves = new HashSet<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor myColor = piece.getTeamColor();
        PieceType myType = piece.getPieceType();

        record Vector(int row, int col) {}
        ArrayList<Vector> probeVectors = new ArrayList<>();
        ArrayList<Vector> possibleEndPositionVectors = new ArrayList<>();

        switch (myType) {
            case PieceType.BISHOP:
                probeVectors.add(new Vector(1,1));
                probeVectors.add(new Vector(-1,1));
                probeVectors.add(new Vector(1,-1));
                probeVectors.add(new Vector(-1,-1));
                break;
            case PieceType.KING:
                possibleEndPositionVectors.add(new Vector(1,0));
                possibleEndPositionVectors.add(new Vector(1,1));
                possibleEndPositionVectors.add(new Vector(0,1));
                possibleEndPositionVectors.add(new Vector(-1,1));
                possibleEndPositionVectors.add(new Vector(-1,0));
                possibleEndPositionVectors.add(new Vector(-1,-1));
                possibleEndPositionVectors.add(new Vector(0,-1));
                possibleEndPositionVectors.add(new Vector(1,-1));
                break;
            case PieceType.KNIGHT:
                possibleEndPositionVectors.add(new Vector(1,2));
                possibleEndPositionVectors.add(new Vector(1,-2));
                possibleEndPositionVectors.add(new Vector(-1,2));
                possibleEndPositionVectors.add(new Vector(-1,-2));
                possibleEndPositionVectors.add(new Vector(2,1));
                possibleEndPositionVectors.add(new Vector(2,-1));
                possibleEndPositionVectors.add(new Vector(-2,1));
                possibleEndPositionVectors.add(new Vector(-2,-1));
                break;
            case PieceType.PAWN:

                // Standard movement
                if (myColor == ChessGame.TeamColor.BLACK)
                {
                    possibleEndPositionVectors.add( new Vector(-1, 0));

                    // Bonus first move
                    if (row == 7)
                    {
                        possibleEndPositionVectors.add( new Vector(-2, 0));
                    }
                } else if (myColor == ChessGame.TeamColor.WHITE)
                {
                    possibleEndPositionVectors.add( new Vector(1, 0));

                    // Bonus first move
                    if (row == 2)
                    {
                        possibleEndPositionVectors.add( new Vector(2, 0));
                    }
                }



                break;
        }

        for (Vector p : probeVectors)
        {
            int probeRow = row;
            int probeCol = col;
            ChessPosition endPosition = new ChessPosition(probeRow, probeCol);
            while (board.hasPosition(endPosition)) {
                probeRow += p.row;
                probeCol += p.col;
                endPosition = new ChessPosition(probeRow, probeCol);
                if (!board.hasPosition(endPosition))
                    break;
                ChessPiece endPiece = board.getPiece(endPosition);
                if (endPiece == null)
                    moves.add(new ChessMove(myPosition, endPosition));
                else {
                    ChessGame.TeamColor endColor = endPiece.getTeamColor();
                    if (endColor != myColor)
                        moves.add(new ChessMove(myPosition, endPosition));
                    break;
                }
            }
        }

        for (Vector p : possibleEndPositionVectors)
        {
            ChessPosition endPosition = new ChessPosition(row + p.row, col + p.col);
            if (board.hasPosition(endPosition))
            {
                ChessPiece endPiece = board.getPiece(endPosition);
                if (endPiece != null && (endPiece.getTeamColor() == myColor))
                    continue;
                moves.add(new ChessMove(myPosition, endPosition));
            }
        }

        return moves;
    }
}
