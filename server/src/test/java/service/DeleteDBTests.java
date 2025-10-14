package service;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;

public class DeleteDBTests extends EndpointTests {

    @Test
    @Order(1)
    @DisplayName("Delete non-empty database")
    public void deleteDatabase() {
        Assertions.assertDoesNotThrow(() -> service.register(new UserData("Test McTestFace", "securePassword", "the_real_test_mctestface@test.com")));
        Assertions.assertDoesNotThrow(() -> service.login("Test McTestFace", "securePassword"));
        Assertions.assertDoesNotThrow(() -> service.createGame("Checkers"));
        Assertions.assertDoesNotThrow(() -> service.deleteDB());
        assertEmptyDatabase();
    }

}
