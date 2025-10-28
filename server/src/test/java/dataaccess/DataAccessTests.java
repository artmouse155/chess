package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Set;

public class DataAccessTests {

    static DataAccess dataAccess;
    final static UserData TEST_USER = new UserData("test1234", "soSecure!!!", "test@gmail.com");
    final static AuthData TEST_AUTH = new AuthData("auth1234", TEST_USER.username());
    final static GameData TEST_GAME = new GameData(1111, null, null, "Checkers", new ChessGame());


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
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(TEST_USER));
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(TEST_AUTH));
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(TEST_GAME));
        var db = Assertions.assertDoesNotThrow(() -> dataAccess.getDB(), "getDB threw an exception");
        Assertions.assertFalse(db.isEmpty());
        Assertions.assertEquals(Set.of(TEST_USER), db.get("userDataSet"), String.format("userDataSet was empty. (%s)",
                db.get("userDataSet").toString()));
        Assertions.assertEquals(Set.of(TEST_AUTH), db.get("authDataSet"), String.format("authDataSet was empty. (%s)",
                db.get("authDataSet").toString()));
        Assertions.assertEquals(Set.of(TEST_GAME), db.get("gameDataSet"), String.format("gameDataSet was empty. (%s)",
                db.get("gameDataSet").toString()));

    }

    // Get DB Negative
    @Test
    public void getEmptyDB() {
        assertEmptyDatabase();
    }

    // Clear DB Positive
    @Test
    public void clearDBPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(TEST_USER));
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(TEST_AUTH));
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(TEST_GAME));
        Assertions.assertDoesNotThrow(() -> dataAccess.deleteDB());
        assertEmptyDatabase();
    }

    // UserData tests

    // get user Positive
    @Test
    public void getUserPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(TEST_USER));
        UserData resultUser = Assertions.assertDoesNotThrow(() ->
                dataAccess.getUser(TEST_USER.username()));
        Assertions.assertEquals(TEST_USER, resultUser);
    }

    // get user Negative
    @Test
    public void getUserNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.getUser(TEST_USER.username()));
    }

    // has user Positive
    @Test
    public void hasUserPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(TEST_USER));
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasUser(TEST_USER.username()));
        Assertions.assertTrue(result);
    }

    // has user Negative
    @Test
    public void hasUserNegative() {
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasUser(TEST_USER.username()));
        Assertions.assertFalse(result);
    }

    // create user Positive
    @Test
    public void createUserPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(TEST_USER));
    }

    // create user Negative
    @Test
    public void createUserNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.createUser(new UserData(null, null, null)));
    }

    // AuthData tests

    // get auth Positive
    @Test
    public void getAuthPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(TEST_AUTH));
        AuthData resultAuth = Assertions.assertDoesNotThrow(() ->
                dataAccess.getAuth(TEST_AUTH.authToken()));
        Assertions.assertEquals(TEST_AUTH, resultAuth);
    }

    // get auth Negative
    @Test
    public void getAuthNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.getAuth(TEST_AUTH.authToken()));
    }

    // has auth Positive
    @Test
    public void hasAuthPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(TEST_AUTH));
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasAuth(TEST_AUTH.authToken()));
        Assertions.assertTrue(result);
    }

    // has auth Negative
    @Test
    public void hasAuthNegative() {
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasAuth(TEST_AUTH.authToken()));
        Assertions.assertFalse(result);
    }

    // create auth Positive
    @Test
    public void createAuthPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(TEST_AUTH));
    }

    // create auth Negative
    @Test
    public void createAuthNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.createAuth(new AuthData(null, null)));
    }

    // remove auth Positive
    @Test
    public void removeAuthPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.removeAuth(TEST_AUTH.authToken()));
    }

    // remove auth Negative
    @Test
    public void removeAuthNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.removeAuth(null));
    }

    // remove auth removes
    @Test
    public void removeAuthRemoves() {
        Assertions.assertDoesNotThrow(() -> dataAccess.createAuth(TEST_AUTH));
        Assertions.assertDoesNotThrow(() -> dataAccess.removeAuth(TEST_AUTH.authToken()));
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasAuth(TEST_AUTH.authToken()));
        Assertions.assertFalse(result);
    }

    // GameData tests

    // get GameData set Positive
    @Test
    public void getGameDatasetPositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(TEST_GAME));
        Set<GameData> resultGameData = Assertions.assertDoesNotThrow(() ->
                dataAccess.getGameDataSet());
        Assertions.assertEquals(Set.of(TEST_GAME), resultGameData);
    }

    // get GameData set Negative
    @Test
    public void getGameDatasetNegative() {
        Set<GameData> resultGameData = Assertions.assertDoesNotThrow(() ->
                dataAccess.getGameDataSet());
        Assertions.assertTrue(resultGameData.isEmpty());
    }

    // get GameData Positive
    @Test
    public void getGamePositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(TEST_GAME));
        GameData resultGameData = Assertions.assertDoesNotThrow(() ->
                dataAccess.getGame(TEST_GAME.gameID()));
        Assertions.assertEquals(TEST_GAME, resultGameData);
    }

    // get gameData Negative
    @Test
    public void getGameNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.getGame(TEST_GAME.gameID()));
    }

    // has gameData Positive
    @Test
    public void hasGamePositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(TEST_GAME));
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasGame(TEST_GAME.gameID()));
        Assertions.assertTrue(result);
    }

    // has gameData Negative
    @Test
    public void hasGameNegative() {
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasGame(TEST_GAME.gameID()));
        Assertions.assertFalse(result);
    }

    // create gameData Positive
    @Test
    public void addGamePositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(TEST_GAME));
    }

    // create gameData Negative
    @Test
    public void addGameNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.addGame(new GameData(-1, null, null, null, null)));
    }

    // UpdateGame Positive
    @Test
    public void updateGamePositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(TEST_GAME));
        var goslingData = TEST_GAME.setWhiteUsername("James Gosling");
        Assertions.assertDoesNotThrow(() -> dataAccess.updateGame(TEST_GAME.gameID(), goslingData));
        GameData resultGameData = Assertions.assertDoesNotThrow(() ->
                dataAccess.getGame(TEST_GAME.gameID()));
        Assertions.assertEquals(goslingData, resultGameData);
    }

    // UpdateGame Negative
    @Test
    public void updateGameNegative() {
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(TEST_GAME));
        var goslingData = TEST_GAME.setWhiteUsername("James Gosling");
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.updateGame(-105, goslingData));
    }

    // remove gameData Positive
    @Test
    public void removeGamePositive() {
        Assertions.assertDoesNotThrow(() -> dataAccess.removeGame(TEST_GAME.gameID()));
    }

    // remove gameData Negative
    @Test
    public void removeGameNegative() {
        Assertions.assertThrowsExactly(DataAccessException.class, () -> dataAccess.removeGame(-100));
    }

    // remove gameData removes
    @Test
    public void removeGameRemoves() {
        Assertions.assertDoesNotThrow(() -> dataAccess.addGame(TEST_GAME));
        Assertions.assertDoesNotThrow(() -> dataAccess.removeGame(TEST_GAME.gameID()));
        boolean result = Assertions.assertDoesNotThrow(() ->
                dataAccess.hasGame(TEST_GAME.gameID()));
        Assertions.assertFalse(result);
    }
}
