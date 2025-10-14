package service;

import handler.Handler;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;

public class EndpointTests {

    protected static Handler handler;

    @BeforeAll
    public static void init() {
        handler = new Handler();
    }

    protected void assertEmptyDatabase() {
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

}
