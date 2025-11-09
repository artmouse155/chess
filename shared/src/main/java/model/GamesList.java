package model;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public record GamesList(Set<GameDataStripped> games) {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public GamesList simplyNumbered() {
        var simplyNumberedGames = new HashSet<GameDataStripped>();
        int index = 0;
        for (final var game : games) {
            simplyNumberedGames.add(new GameDataStripped(
                    index,
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
            index++;
        }
        return new GamesList(simplyNumberedGames);
    }

}
