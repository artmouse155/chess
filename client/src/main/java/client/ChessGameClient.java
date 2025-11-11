package client;

import chess.ChessGame;
import model.AuthData;

import java.util.Objects;

public class ChessGameClient extends Client {

    public enum JoinType {
        BLACK,
        OBSERVER,
        WHITE

    }

    private final JoinType joinType;
    private final int gameID;

    public ChessGameClient(JoinType joinType, AuthData authData, int gameID) throws ClientException {
        this.joinType = joinType;
        this.gameID = gameID;

        // TODO: Update with WebSocket code for phase six. This is a dummy function that pretends to call the server to see if you are authenticated.
        if (Objects.equals(authData.authToken(), "")) {
            throw new ClientException("Bad Authentication");
        }
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
