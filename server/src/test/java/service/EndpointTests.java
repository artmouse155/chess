package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;

public class EndpointTests {

    protected static Service service;

    @BeforeAll
    public static void init() {
        service = new Service();
    }

    protected void assertEmptyDatabase() {
        var db = Assertions.assertDoesNotThrow(() -> service.getDB(), "handleDeleteDB threw an exception");
        Assertions.assertTrue(db.get("userDataSet").isEmpty(), "userDataSet was not empty.");
        Assertions.assertTrue(db.get("authDataSet").isEmpty(), "authDataSet was not empty.");
        Assertions.assertTrue(db.get("gameDataSet").isEmpty(), "gameDataSet was not empty.");
    }

    // Assert empty database
    @BeforeEach
    public void setup() {
        assertEmptyDatabase();
    }

}
