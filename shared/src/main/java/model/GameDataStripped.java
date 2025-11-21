package model;

import chess.ChessGame;

public record GameDataStripped(int gameID, String whiteUsername, String blackUsername, String gameName, GameData.GameState gameState) {

    public GameDataStripped(int gameID, String whiteUsername, String blackUsername, String gameName) {
        this(gameID, whiteUsername, blackUsername, gameName, GameData.GameState.ACTIVE);
    }

}