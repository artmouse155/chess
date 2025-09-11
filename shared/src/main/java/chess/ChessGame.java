package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor currentColor;
    ChessBoard board;
    public ChessGame() {
        currentColor = TeamColor.WHITE;
        board = new ChessBoard();
    }

    public ChessGame(TeamColor currentColor, ChessBoard board)
    {
        this.currentColor = currentColor;
        this.board = board;
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

        if (piece != null)
        {
            TeamColor color = piece.getTeamColor();

            HashSet<ChessMove> moves = new HashSet<>();
            for (ChessMove m : piece.pieceMoves(board, startPosition))
            {
                if (!WouldMovePutKingInCheck(m, currentColor)) {
                    moves.add(m);
                }
            }

            return moves;
        }

        return null;
    }

    private Collection<ChessMove> allValidMoves(TeamColor color, ChessPosition endPosition, boolean skipInvaldCheck) {
        HashSet<ChessMove> moves = new HashSet<>();
        HashSet<ChessPosition> teamPositions = (HashSet<ChessPosition>) board.getAllPositions(color);
        for (ChessPosition p : teamPositions)
        {
            for (ChessMove m : (skipInvaldCheck)?board.getPiece(p).pieceMoves(board,p):validMoves(p))
            {
                if (endPosition == null || m.getEndPosition().equals(endPosition)) {
                    moves.add(m);
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> allValidMoves(TeamColor color) {
        return allValidMoves(color, null, false);
    }

    private boolean WouldMovePutKingInCheck(ChessMove move, TeamColor color) {

        ChessGame.TeamColor opposingColor = getOpposingTeam(color);


        // Get the positions of all opposing pieces
        HashSet<ChessPosition> offenders = (HashSet<ChessPosition>) board.getAllPositions(opposingColor);

        // 3. Simulate a board where the move was completed - IMPORTANT: if this simulation defeats
        // one of the pieces that could potentially reach the king, don't calculate it

        ChessBoard copyBoard = board.copyAndForceMove(move);

        for (ChessPosition offenderPosition : offenders)
        {
            ChessPiece offenderPiece = board.getPiece(offenderPosition);
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

        // 4. See if any of these pieces can reach the king - IMPORTANT: Don't calculate with validMoves
        // here, as this would cause infinite recursion. And it doesn't matter if that move would put
        // their own king in check, because the game would be over by then.

        // 5. Return the result of step 4.

//        ChessGame copyGame = new ChessGame(color, board.copyAndForceMove(m));
//        if (!copyGame.isInCheck(color))
//        {
//            moves.add(m);
//        }
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

        if (piece != null && piece.getTeamColor() == currentColor && moves != null && moves.contains(move))
        {
            ChessPiece.PieceType type = piece.getPieceType();
            ChessPiece.PieceType promotionType = move.getPromotionPiece();
            board.addPiece(endPosition, new ChessPiece(currentColor,(promotionType==null)?type:promotionType));
            board.addPiece(startPosition, null);

            setTeamTurn(getOpposingTeam(currentColor));
        } else {
            throw new InvalidMoveException("Invalid Move");
        }
    }

    private TeamColor getOpposingTeam(TeamColor color) {
        if (currentColor == TeamColor.WHITE)
        {
            return TeamColor.BLACK;
        } else if (currentColor == TeamColor.BLACK)
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
        return !allValidMoves(getOpposingTeam(teamColor), board.getKingPosition(teamColor), false).isEmpty();
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
}
