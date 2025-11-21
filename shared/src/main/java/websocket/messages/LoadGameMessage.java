package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    private final ChessGame game;

    public LoadGameMessage(ChessGame chessGame) {
        super(ServerMessageType.LOAD_GAME);
        this.game = chessGame;
    }

    public ChessGame getChessGame() {
        return game;
    }
}
