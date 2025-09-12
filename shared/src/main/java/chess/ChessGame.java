package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentColor;
    private ChessBoard board;

    public ChessGame() {
        currentColor = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {return null;}

        TeamColor color = piece.getTeamColor();

        HashSet<ChessMove> moves = new HashSet<>();
        for (ChessMove m : piece.pieceMoves(board, startPosition))
        {
            if (wouldMovePutKingInCheck(m, color)) {
                continue;
            }
            if (m.getSpecialMove() == ChessMove.SpecialMove.CASTLE)
            {
                // check that king is not currently in check
                if (isInCheck(color))
                {
                    continue;
                }
                // check that none of the intermediate spots would put the king in check
                // If we moved right
                int row = m.getStartPosition().getRow();
                if (m.getEndPosition().getColumn() == 7)
                {
                    if (wouldMovePutKingInCheck(
                            new ChessMove(startPosition,
                            new ChessPosition(row, 6)),
                            color)
                    )
                    {
                        continue;
                    }


                } else if (m.getEndPosition().getColumn() == 3) // If we moved left
                {
                    if (wouldMovePutKingInCheck(
                            new ChessMove(startPosition,
                                    new ChessPosition(row, 4)),
                            color)
                    )
                    {
                        continue;
                    }
                }
                // we already checked the final position for being in check up above
            }
                moves.add(m);
        }

        return moves;

    }

    private Collection<ChessMove> allValidMoves(TeamColor color, ChessPosition endPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        HashSet<ChessPosition> teamPositions = (HashSet<ChessPosition>) board.getAllPositions(color);
        for (ChessPosition p : teamPositions)
        {
            for (ChessMove m : validMoves(p))
            {
                if (endPosition == null || m.getEndPosition().equals(endPosition)) {
                    moves.add(m);
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> allValidMoves(TeamColor color) {
        return allValidMoves(color, null);
    }

    private boolean wouldMovePutKingInCheck(ChessMove move, TeamColor color) {

        ChessGame.TeamColor opposingColor = getOpposingTeam(color);

        // Create the copy board for testing
        ChessBoard copyBoard = board.copyAndForceMove(move);

        // Get the positions of all opposing pieces
        HashSet<ChessPosition> offenders = (HashSet<ChessPosition>) copyBoard.getAllPositions(opposingColor);

        for (ChessPosition offenderPosition : offenders)
        {
            ChessPiece offenderPiece = copyBoard.getPiece(offenderPosition);
            // Just in case we accidentally reference a piece that got destroyed, check if color matches
            if (offenderPiece.getTeamColor() == opposingColor) {
                HashSet<ChessMove> offenderMoves = (HashSet<ChessMove>) offenderPiece.pieceMoves(copyBoard, offenderPosition);
                for (ChessMove offenderMove : offenderMoves) {
                    if (offenderMove.getEndPosition().equals(copyBoard.getKingPosition(color))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        HashSet<ChessMove> moves = (HashSet<ChessMove>) validMoves(startPosition);
        ChessPiece piece = board.getPiece(startPosition);



        if (piece != null && piece.getTeamColor() == currentColor && moves != null)
        {

            boolean moveFound = false;
            for (ChessMove testMove : moves)
            {
                if (testMove.equals(move))
                {
                    // Allows passing along special moves
                    move = testMove;
                    moveFound = true;
                    break;
                }

            }

            if (!moveFound)
            {
                throw new InvalidMoveException("Invalid Move");
            }

            ChessPiece.PieceType type = piece.getPieceType();
            ChessPiece.PieceType promotionType = move.getPromotionPiece();
            ChessMove.SpecialMove specialMove = move.getSpecialMove();


            switch (specialMove) {
                case EN_PASSANT -> makeEnPassantMove(move);
                case CASTLE -> makeCastleMove(move);
                case null, default -> {
                    // We set en passant flag IFF (1) we are a pawn and (2) we will attempt to move 2+ spaces
                    if (type == ChessPiece.PieceType.PAWN && (abs(startPosition.getRow() - endPosition.getRow()) > 1)) {
                        board.setLastPawnMoveTwicePosition(endPosition);
                    } else {
                        board.setLastPawnMoveTwicePosition(null);
                    }

                    board.removePiece(startPosition);
                    board.addPiece(endPosition, new ChessPiece(currentColor, (promotionType == null) ? type : promotionType, true));
                }
            }

            setTeamTurn(getOpposingTeam(currentColor));
        } else {
            throw new InvalidMoveException("Invalid Move");
        }
    }

    private void makeEnPassantMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        board.removePiece(startPosition);
        board.removePiece(new ChessPosition(startPosition.getRow(), endPosition.getColumn()));
        board.addPiece(endPosition, new ChessPiece(currentColor, ChessPiece.PieceType.PAWN, true));
    }

    private void makeCastleMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        // Move the king to the final position
        board.removePiece(startPosition);
        board.addPiece(endPosition, new ChessPiece(currentColor, ChessPiece.PieceType.KING, true));

        // move the rook on the side the king is castling on the side of the board closer to the center
        // If we moved right
        if (endPosition.getColumn() == 7) {
            board.removePiece(new ChessPosition(startPosition.getRow(), 8));
            board.addPiece(new ChessPosition(startPosition.getRow(), 6), new ChessPiece(currentColor, ChessPiece.PieceType.ROOK, true));
        } else if (endPosition.getColumn() == 3) // If we moved left
        {
            board.removePiece(new ChessPosition(startPosition.getRow(), 1));
            board.addPiece(new ChessPosition(startPosition.getRow(), 4), new ChessPiece(currentColor, ChessPiece.PieceType.ROOK, true));
        }
    }

    private TeamColor getOpposingTeam(TeamColor color) {
        if (color == TeamColor.WHITE)
        {
            return TeamColor.BLACK;
        } else if (color == TeamColor.BLACK)
        {
            return TeamColor.WHITE;
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return !allValidMoves(getOpposingTeam(teamColor), board.getKingPosition(teamColor)).isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (allValidMoves(teamColor).isEmpty() && isInCheck(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (allValidMoves(teamColor).isEmpty() && !isInCheck(teamColor));
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentColor == chessGame.currentColor && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentColor, board);
    }
}
