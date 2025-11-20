package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    private final ChessGame chessGame;

    public LoadGameMessage(ChessGame chessGame) {
        super(ServerMessageType.LOAD_GAME);
        this.chessGame = chessGame;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }
}
