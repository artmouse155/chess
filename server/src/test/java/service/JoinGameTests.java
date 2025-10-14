package service;

import handler.exception.AlreadyTakenException;
import handler.exception.BadRequestException;
import handler.exception.UnauthorizedException;
import model.AuthData;
import model.CreateGameResponse;
import model.UserData;
import org.junit.jupiter.api.*;

public class JoinGameTests extends EndpointTests {

    final private String gameName = "thisGame";
    private AuthData authData;
    private int gameID;

    @BeforeEach
    public void beforeEach() {
        registerTestUser();
        authData = loginTestUser();
        gameID = Assertions.assertDoesNotThrow(() -> handler.handleCreateGame(gameName)).gameID();
    }

    @Test
    @Order(1)
    @DisplayName("Empty username")
    public void emptyUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame("", "BLACK", gameID));
    }

    @Test
    @Order(2)
    @DisplayName("Null username")
    public void nullUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(null, "BLACK", gameID));
    }

    @Test
    @Order(3)
    @DisplayName("Username not in database")
    public void usernameNotInDatabase() {
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleJoinGame("JamesGosling12345", "BLACK", gameID));
    }

    @Test
    @Order(4)
    @DisplayName("Username not in active session")
    public void usernameNotInSession() {
        Assertions.assertDoesNotThrow(() -> handler.handleLogout(authData.authToken()));
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleJoinGame(testUser.username(), "BLACK", gameID));
    }

    @Test
    @Order(5)
    @DisplayName("Empty playerColor")
    public void emptyPlayerColor() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(testUser.username(), "", gameID));
    }

    @Test
    @Order(6)
    @DisplayName("Null playerColor")
    public void nullPlayerColor() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(testUser.username(), "", gameID));
    }

    @Test
    @Order(7)
    @DisplayName("Invalid playerColor")
    public void invalidPlayerColor() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(testUser.username(), "GREEN", gameID));
    }

    @Test
    @Order(8)
    @DisplayName("Lowercase playerColor")
    public void lowercasePlayerColor() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(testUser.username(), "black", gameID));
    }

    @Test
    @Order(9)
    @DisplayName("Incorrect gameID")
    public void incorrectGameID() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(testUser.username(), "BLACK", 8675309));
    }

    @Test
    @Order(10)
    @DisplayName("Negative gameID")
    public void negativeGameID() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(testUser.username(), "BLACK", -12));
    }

    @Test
    @Order(11)
    @DisplayName("Very large gameID")
    public void veryLargeGameID() {
        int veryLarge = Double.valueOf(Math.pow(10, 100)).intValue();
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(testUser.username(), "BLACK", veryLarge));
    }

    @Test
    @Order(11)
    @DisplayName("Join in occupied spot")
    public void alreadyTaken() {
        // Register and login new user
        String secondUsername = "username dos";
        String secondPassword = "abc123";
        Assertions.assertDoesNotThrow(() -> handler.handleRegister(new UserData(secondUsername, "abc123", "second@gmail.com")));
        Assertions.assertDoesNotThrow(() -> handler.handleLogin(secondUsername, secondPassword));

        Assertions.assertDoesNotThrow(() -> handler.handleJoinGame(testUser.username(), "BLACK", gameID));
        Assertions.assertThrowsExactly(AlreadyTakenException.class, () -> handler.handleJoinGame(secondUsername, "BLACK", gameID));
    }

}
