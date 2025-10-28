package dataaccess;

import org.junit.jupiter.api.*;

public class DataAccessTests {

    static DataAccess dataAccess;

    @BeforeAll
    public static void init() {
        Assertions.assertDoesNotThrow(() -> {
            dataAccess = new SQLDataAccess();
        });
    }

    protected void assertEmptyDatabase() {
        var db = Assertions.assertDoesNotThrow(() -> dataAccess.getDB(), "getDB threw an exception");
        Assertions.assertFalse(db.isEmpty());
        Assertions.assertTrue(db.get("userDataSet").isEmpty(), String.format("userDataSet was not empty. (%s)", db.get("userDataSet").toString()));
        Assertions.assertTrue(db.get("authDataSet").isEmpty(), String.format("authDataSet was not empty. (%s)", db.get("authDataSet").toString()));
        Assertions.assertTrue(db.get("gameDataSet").isEmpty(), String.format("gameDataSet was not empty. (%s)", db.get("gameDataSet").toString()));
    }

    // Assert empty database
    @BeforeEach
    public void setup() {
        Assertions.assertDoesNotThrow(() -> dataAccess.deleteDB());
        assertEmptyDatabase();
    }

    // Get DB Positive
    @Test
    @Order(1)
    @DisplayName("Get non-empty DB")
    public void getNonEmptyDB() {

    }

    // Get DB Negative
    @Test
    @Order(2)
    @DisplayName("Get empty DB")
    public void getEmptyDB() {
        assertEmptyDatabase();
    }
}
