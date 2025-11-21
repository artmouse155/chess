package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, GameState gameState) {

    public enum GameState {
        ACTIVE,
        RESIGNED
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

    public GameData resign() {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game, GameState.RESIGNED);
    }

    public GameDataStripped stripped() {
        return new GameDataStripped(gameID, whiteUsername, blackUsername, gameName, gameState);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
