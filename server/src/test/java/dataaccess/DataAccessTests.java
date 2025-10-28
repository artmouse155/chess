package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.TestUser;

import java.util.Set;

public class DataAccessTests {

    static DataAccess dataAccess;
    final static UserData testUser = new UserData("test1234", "soSecure!!!", "test@gmail.com");
    final static AuthData testAuth = new AuthData("auth1234", testUser.username());
    final static GameData testGame = new GameData(1111, null, null, "Checkers", new ChessGame());


    @BeforeAll
    public static void init() {
        Assertions.assertDoesNotThrow(() -> {
            dataAccess = new SQLDataAccess();
        });
    }

    protected void assertEmptyDatabase() {
        var db = Assertions.assertDoesNotThrow(() -> dataAccess.getDB(), "getDB threw an exception");
        Assertions.assertFalse(db.isEmpty(), "getDB returned empty database.");
        Assertions.assertTrue(db.containsKey("userDataSet"), "the database returned by getDB did not have the key \"userDataSet\".");
        Assertions.assertTrue(db.containsKey("authDataSet"), "the database returned by getDB did not have the key \"authDataSet\".");
        Assertions.assertTrue(db.containsKey("gameDataSet"), "the database returned by getDB did not have the key \"gameDataSet\".");
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
    public void getNonEmptyDB() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(testUser));
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(testAuth));
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(testGame));
        var db = Assertions.assertDoesNotThrow(() -> dataAccess.getDB(), "getDB threw an exception");
        Assertions.assertFalse(db.isEmpty());
        Assertions.assertEquals(Set.of(testUser), db.get("userDataSet"), String.format("userDataSet was empty. (%s)", db.get("userDataSet").toString()));
        Assertions.assertEquals(Set.of(testAuth), db.get("authDataSet"), String.format("authDataSet was empty. (%s)", db.get("authDataSet").toString()));
        Assertions.assertEquals(Set.of(testGame), db.get("gameDataSet"), String.format("gameDataSet was empty. (%s)", db.get("gameDataSet").toString()));

    }

    // Get DB Negative
    @Test
    public void getEmptyDB() {
        assertEmptyDatabase();
    }

    // Clear DB Positive
    @Test
    public void clearDBPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(testUser));
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(testAuth));
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(testGame));
        Assertions.assertDoesNotThrow(() -> dataAccess.deleteDB());
        assertEmptyDatabase();
    }

    // get user Positive
    @Test
    public void getUserPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(testUser));
        UserData resultUser = Assertions.assertDoesNotThrow(() ->
                dataAccess.getUser(testUser.username()));
        Assertions.assertEquals(testUser, resultUser);
    }

    // get user Negative
    @Test
    public void getUserNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.getUser(testUser.username()));
    }

    // has user Positive
    @Test
    public void hasUserPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(testUser));
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasUser(testUser.username()));
        Assertions.assertTrue(result);
    }

    // has user Negative
    @Test
    public void hasUserNegative() {
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasUser(testUser.username()));
        Assertions.assertFalse(result);
    }

    // create user Positive
    @Test
    public void createUserPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(testUser));
    }

    // create user Negative
    @Test
    public void createUserNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.createUser(new UserData(null, null, null)));
    }

    // get auth Positive
    @Test
    public void getAuthPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(testAuth));
        AuthData resultAuth = Assertions.assertDoesNotThrow(() ->
                dataAccess.getAuth(testAuth.authToken()));
        Assertions.assertEquals(testAuth, resultAuth);
    }

    // get auth Negative
    @Test
    public void getAuthNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.getAuth(testAuth.authToken()));
    }

    // has auth Positive
    @Test
    public void hasAuthPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(testAuth));
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasAuth(testAuth.authToken()));
        Assertions.assertTrue(result);
    }

    // has auth Negative
    @Test
    public void hasAuthNegative() {
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasAuth(testAuth.username()));
        Assertions.assertFalse(result);
    }

    // create auth Positive
    @Test
    public void createAuthPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(testAuth));
    }

    // create auth Negative
    @Test
    public void createAuthNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.createAuth(new AuthData(null, null)));
    }

    // remove auth Positive
    @Test
    public void removeAuthPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.removeAuth(testAuth.authToken()));
    }

    // remove auth Negative
    @Test
    public void removeAuthNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.removeAuth(null));
    }

    // remove auth removes
    @Test
    public void removeAuthRemoves() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(testAuth));
        Assertions.assertDoesNotThrow(() -> dataAccess.removeAuth(testAuth.authToken()));
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasAuth(testAuth.username()));
        Assertions.assertFalse(result);
    }
}
