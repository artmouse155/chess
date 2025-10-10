package model;

import com.google.gson.Gson;

public record GameDataStripped(int gameID, String whiteUsername, String blackUsername, String gameName) {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}