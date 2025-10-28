package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Set;

public class DataAccessTests {

    static DataAccess dataAccess;
    protected static UserData testUser = new UserData("test1234", "soSecure!!!", "test@gmail.com");


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

        final var testAuth = new AuthData("auth1234", testUser.username());
        final var testGame = new GameData(1111, null, null, "Checkers", new ChessGame());

        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(testUser));
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(testAuth));
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(testGame));
        var db = Assertions.assertDoesNotThrow(() -> dataAccess.getDB(), "getDB threw an exception");
        Assertions.assertFalse(db.isEmpty());
        Assertions.assertEquals(db.get("userDataSet"), Set.of(testUser), String.format("userDataSet was not empty. (%s)", db.get("userDataSet").toString()));
        Assertions.assertEquals(db.get("authDataSet"), Set.of(testAuth), String.format("authDataSet was not empty. (%s)", db.get("authDataSet").toString()));
        Assertions.assertEquals(db.get("gameDataSet"), Set.of(testGame), String.format("gameDataSet was not empty. (%s)", db.get("gameDataSet").toString()));

    }

    // Get DB Negative
    @Test
    @Order(2)
    @DisplayName("Get empty DB")
    public void getEmptyDB() {
        assertEmptyDatabase();
    }
}
