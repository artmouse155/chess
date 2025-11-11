package client;

import chess.ChessGame;
import model.AuthData;

public class ChessGameClient extends Client {

    public enum JoinType {
        BLACK,
        OBSERVER,
        WHITE

    }

    private final JoinType joinType;
    private final int gameID;

    public ChessGameClient(JoinType joinType, AuthData authData, int gameID) {
        this.joinType = joinType;
        this.gameID = gameID;

    }

    @Override
    public void run() {
        System.out.println("THIS IS THE CHESS GAME CLIENT. YOU ARE ON TEAM " + joinType.toString());
    }

    @Override
    protected void printPrompt() {

    }

    @Override
    protected String formatError(ClientException ex) {
        return "";
    }

    @Override
    protected String eval(String input) {
        return "";
    }
}
