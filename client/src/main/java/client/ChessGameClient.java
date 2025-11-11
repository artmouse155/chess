package client;

import chess.ChessGame;
import model.AuthData;

public class ChessGameClient extends Client {

    public enum JoinType {
        BLACK,
        OBSERVER,
        WHITE

    }

    public ChessGameClient(JoinType joinType, AuthData authData, int gameID) {

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
