package service;

import handler.Handler;
import model.AuthData;
import model.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;

public class EndpointTests {

    protected static Handler handler;
    protected static UserData testUser = new UserData("test1234", "soSecure!!!", "test@gmail.com");

    @BeforeAll
    public static void init() {
        handler = new Handler(true);
    }

    protected void assertEmptyDatabase() {
        var db = Assertions.assertDoesNotThrow(() -> handler.handleGetDB(), "handleDeleteDB threw an exception");
        Assertions.assertTrue(db.get("userDataSet").isEmpty(), String.format("userDataSet was not empty. (%s)", db.get("userDataSet").toString()));
        Assertions.assertTrue(db.get("authDataSet").isEmpty(), String.format("authDataSet was not empty. (%s)", db.get("authDataSet").toString()));
        Assertions.assertTrue(db.get("gameDataSet").isEmpty(), String.format("gameDataSet was not empty. (%s)", db.get("gameDataSet").toString()));
    }

    public void registerTestUser() {
        Assertions.assertDoesNotThrow(() -> handler.handleRegister(testUser));
    }

    public AuthData loginTestUser() {
        return Assertions.assertDoesNotThrow(() -> handler.handleLogin(new LoginRequest(testUser.username(), testUser.password())));
    }

    // Assert empty database
    @BeforeEach
    public void setup() {
        Assertions.assertDoesNotThrow(() -> handler.handleDeleteDB());
        assertEmptyDatabase();
    }

}
