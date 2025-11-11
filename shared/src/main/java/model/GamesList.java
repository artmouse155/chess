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
        int loop_index = 0;
        for (final var game : games) {
            simplyNumberedGames.add(new GameDataStripped(
                    loop_index,
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
            loop_index++;
        }
        return new GamesList(simplyNumberedGames);
    }

    public int gameIDAtIndex(int index) {
        int loop_index = 0;
        for (final var game : games) {
            if (loop_index == index) {
                return game.gameID();
            }
            loop_index++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

}
