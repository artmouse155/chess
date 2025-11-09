package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record JoinGameRequest(ChessGame.TeamColor playerColor, int gameID) {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
