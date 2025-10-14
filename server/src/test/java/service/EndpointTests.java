package service;

import handler.Handler;
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
        Assertions.assertTrue(db.get("userDataSet").isEmpty(), String.format("userDataSet was not empty. (%s)", db.get("userDataSet").toString()));
        Assertions.assertTrue(db.get("authDataSet").isEmpty(), String.format("authDataSet was not empty. (%s)", db.get("authDataSet").toString()));
        Assertions.assertTrue(db.get("gameDataSet").isEmpty(), String.format("gameDataSet was not empty. (%s)", db.get("gameDataSet").toString()));
    }

    // Assert empty database
    @BeforeEach
    public void setup() {
        Assertions.assertDoesNotThrow(() -> handler.handleDeleteDB());
        assertEmptyDatabase();
    }

}
