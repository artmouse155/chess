package model;

import com.google.gson.Gson;

record LoginResponse(String username, String authToken) {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
