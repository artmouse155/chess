package model;

import com.google.gson.Gson;

public record CreateGameRequest(String gameName) {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
