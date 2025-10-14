package model;

import java.util.Set;

public record GamesList(Set<GameDataStripped> games) {

    @Override
    public String toString() {
        return "";
    }

}
