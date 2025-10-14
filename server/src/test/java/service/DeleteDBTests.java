package service;

import handler.Handler;
import handler.ResponseException;
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
        Assertions.assertTrue(db.get("userDataSet").isEmpty());
        Assertions.assertTrue(db.get("authDataSet").isEmpty());
        Assertions.assertTrue(db.get("gameDataSet").isEmpty());
    }

    // Assert empty database
    @BeforeEach
    public void setup() {
        Assertions.assertDoesNotThrow(() -> handler.handleDeleteDB(), "handleDeleteDB threw an exception");
        assertEmptyDatabase();
    }


}
