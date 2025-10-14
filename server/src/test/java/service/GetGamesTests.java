package service;

import handler.exception.BadRequestException;
import model.GameDataStripped;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class GetGamesTests extends EndpointTests {

    @Test
    @Order(1)
    @DisplayName("Get games after failed game creation")
    public void getGamesAfterFailed() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleCreateGame(null));
        var getGamesResponse = Assertions.assertDoesNotThrow(() -> handler.handleGetGames());
        Assertions.assertTrue(() -> getGamesResponse.games().isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Create and get games")
    public void createAndGetGames() {
        String gameName = "testGame";
        var createGameResponse = Assertions.assertDoesNotThrow(() -> handler.handleCreateGame(gameName));
        var getGamesResponse = Assertions.assertDoesNotThrow(() -> handler.handleGetGames());
        Assertions.assertTrue(() -> getGamesResponse.games().equals(Set.of(new GameDataStripped(createGameResponse.gameID(), null, null, gameName))));
    }

}
