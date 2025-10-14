package service;

import handler.Handler;
import handler.ResponseException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;

public class DeleteDBTests {

    private static Handler handler;

    @BeforeAll
    public static void init() {
        handler = new Handler();
    }

    private void assertEmptyDatabase() {
        var db = Assertions.assertDoesNotThrow(() -> handler.handleGetDB(), "handleDeleteDB threw an exception");
        Assertions.assertTrue(db.get("userDataSet").isEmpty(), "userDataSet was not empty.");
        Assertions.assertTrue(db.get("authDataSet").isEmpty(), "authDataSet was not empty.");
        Assertions.assertTrue(db.get("gameDataSet").isEmpty(), "gameDataSet was not empty.");
    }

    // Assert empty database
    @BeforeEach
    public void setup() {
        Assertions.assertDoesNotThrow(() -> handler.handleDeleteDB(), "handleDeleteDB threw an exception");
        assertEmptyDatabase();
    }


    @Test
    @Order(1)
    @DisplayName("Delete non-empty database")
    public void deleteDatabase() {
        Assertions.assertDoesNotThrow(() -> handler.handleRegister(new UserData("Test McTestFace", "securePassword", "the_real_test_mctestface@test.com")));
        Assertions.assertDoesNotThrow(() -> handler.handleLogin("Test McTestFace", "securePassword"));
        Assertions.assertDoesNotThrow(() -> handler.handleCreateGame("Checkers"));
        Assertions.assertDoesNotThrow(() -> handler.handleDeleteDB());
        assertEmptyDatabase();
    }

}
