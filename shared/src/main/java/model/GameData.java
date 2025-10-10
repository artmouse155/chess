package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData setWhiteUsername(String whiteUsername) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public GameData setBlackUsername(String blackUsername) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public GameDataStripped stripped() {
        return new GameDataStripped(gameID, whiteUsername, blackUsername, gameName);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
