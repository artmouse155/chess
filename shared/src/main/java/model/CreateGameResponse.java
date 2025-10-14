package model;

import com.google.gson.Gson;

public record CreateGameResponse(int gameID) {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
