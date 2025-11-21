package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, GameState gameState) {

    enum GameState {
        ACTIVE,
        RESIGN
    }

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this(gameID, whiteUsername, blackUsername, gameName, game, GameState.ACTIVE);
    }

    public GameData setWhiteUsername(String whiteUsername) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game, gameState);
    }

    public GameData setBlackUsername(String blackUsername) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game, gameState);
    }

    public GameDataStripped stripped() {
        return new GameDataStripped(gameID, whiteUsername, blackUsername, gameName, gameState);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
