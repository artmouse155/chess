package service;

import handler.exception.BadRequestException;
import model.GameDataStripped;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class CreateGameTests extends EndpointTests {

    @Test
    @Order(1)
    @DisplayName("Create game with empty game name")
    public void createGameEmpty() {
        Assertions.assertThrows(BadRequestException.class, () -> handler.handleCreateGame(""));
    }

    @Test
    @Order(2)
    @DisplayName("Create game with null game name")
    public void createGameNull() {
        Assertions.assertThrows(BadRequestException.class, () -> handler.handleCreateGame(null));
    }

    @Test
    @Order(3)
    @DisplayName("Create game with duplicate name")
    public void createGameDuplicate() {
        String gameName = "coolGame";
        Assertions.assertDoesNotThrow(() -> handler.handleCreateGame(gameName));
        Assertions.assertDoesNotThrow(() -> handler.handleCreateGame(gameName));
    }

    @Test
    @Order(4)
    @DisplayName("Create and get 3 games")
    public void createAndGetGames() {
        var createGameResponse1 = Assertions.assertDoesNotThrow(() -> handler.handleCreateGame("game1"));
        var createGameResponse2 = Assertions.assertDoesNotThrow(() -> handler.handleCreateGame("game2"));
        var createGameResponse3 = Assertions.assertDoesNotThrow(() -> handler.handleCreateGame("game3"));
        var getGamesResponse = Assertions.assertDoesNotThrow(() -> handler.handleGetGames());
        Assertions.assertTrue(() -> getGamesResponse.games().contains(
                new GameDataStripped(createGameResponse1.gameID(), null, null, "game1")
        ));
        Assertions.assertTrue(() -> getGamesResponse.games().contains(

                new GameDataStripped(createGameResponse2.gameID(), null, null, "game2")
        ));
        Assertions.assertTrue(() -> getGamesResponse.games().contains(
                new GameDataStripped(createGameResponse3.gameID(), null, null, "game3")
        ));
    }
}
