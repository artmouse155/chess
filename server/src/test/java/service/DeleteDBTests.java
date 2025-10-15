package service;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;

public class DeleteDBTests extends EndpointTests {

    @Test
    @Order(1)
    @DisplayName("Delete empty database")
    public void deleteEmptyDatabase() {
        Assertions.assertDoesNotThrow(() -> handler.handleGetDB());
        assertEmptyDatabase();
    }

    @Test
    @Order(2)
    @DisplayName("Delete non-empty database")
    public void deleteNonEmptyDatabase() {
        Assertions.assertDoesNotThrow(() -> handler.handleRegister(testUser));
        Assertions.assertDoesNotThrow(() -> handler.handleLogin(testUser.username(), testUser.password()));
        Assertions.assertDoesNotThrow(() -> handler.handleCreateGame("Checkers"));
        Assertions.assertDoesNotThrow(() -> handler.handleDeleteDB());
        assertEmptyDatabase();
    }

}
