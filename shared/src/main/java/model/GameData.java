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
        var strippedData = Map.of("gameID", gameID, "whiteUsername", whiteUsername, "blackUsername", blackUsername, "gameName", gameName);
        return new Gson().toJson(strippedData);
    }

}
