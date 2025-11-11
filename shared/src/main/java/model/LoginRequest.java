package model;

import com.google.gson.Gson;

public record LoginRequest(String username, String password) {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
