package model;

import com.google.gson.Gson;

import java.util.Set;

public record GamesList(Set<GameDataStripped> games) {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
