package client;

import chess.ChessGame;

public class ChessGameClient extends Client {

    // Player
    public ChessGameClient(int gameID, ChessGame.TeamColor teamColor) {

    }

    // Observer
    public ChessGameClient(int gameID) {
        this(gameID, null);
    }

    @Override
    public void run() {

    }

    @Override
    protected void printPrompt() {

    }

    @Override
    protected String formatError(ClientException ex) {
        return "";
    }

    @Override
    public String eval(String input) {
        return "";
    }
}
