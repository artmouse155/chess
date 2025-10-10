package model;

import chess.ChessGame;
import com.google.gson.Gson;

import java.util.Map;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData setWhiteUsername(String whiteUsername) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public GameData setBlackUsername(String blackUsername) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public String toString() {
        // Don't include ChessGame in toString
        record StrippedGameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
        }
        return new Gson().toJson(new StrippedGameData(gameID, whiteUsername, blackUsername, gameName));
    }

}
