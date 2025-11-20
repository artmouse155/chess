package model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record GamesList(List<GameDataStripped> games) {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public GamesList simplyNumbered() {
        var simplyNumberedGames = new ArrayList<GameDataStripped>();
        int loopIndex = 1;
        for (final var game : games) {
            simplyNumberedGames.add(new GameDataStripped(
                    loopIndex,
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
            loopIndex++;
        }
        return new GamesList(simplyNumberedGames);
    }

    // NOTE: ASSUMES INPUT IS 0-INDEXED
    public GameDataStripped gameAtIndex(int index) {
        return games.get(index);
    }

}
