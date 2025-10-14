package service;

import handler.exception.AlreadyTakenException;
import handler.exception.BadRequestException;
import handler.exception.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

public class JoinGameTests extends EndpointTests {

    final private String gameName = "thisGame";
    private AuthData authData;
    private int gameID;

    final private UserData secondUser = new UserData("username dos", "abc123", "second@gmail.com");
    private AuthData secondAuthData;

    @BeforeEach
    public void beforeEach() {
        registerTestUser();
        authData = loginTestUser();
        gameID = Assertions.assertDoesNotThrow(() -> handler.handleCreateGame(gameName)).gameID();

        // Login second user
        Assertions.assertDoesNotThrow(() -> handler.handleRegister(new UserData(secondUser.username(), "abc123", "second@gmail.com")));
        secondAuthData = Assertions.assertDoesNotThrow(() -> handler.handleLogin(secondUser.username(), secondUser.password()));

    }

    @Test
    @Order(1)
    @DisplayName("Empty username")
    public void emptyUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(new AuthData("1234", ""), "BLACK", gameID));
    }

    @Test
    @Order(2)
    @DisplayName("Null username")
    public void nullUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(new AuthData("1234", null), "BLACK", gameID));
    }

    @Test
    @Order(3)
    @DisplayName("Username not in database")
    public void usernameNotInDatabase() {
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleJoinGame(new AuthData("1234", "JamesGosling12345"), "BLACK", gameID));
    }

    @Test
    @Order(4)
    @DisplayName("Username not in active session")
    public void usernameNotInSession() {
        Assertions.assertDoesNotThrow(() -> handler.handleLogout(authData.authToken()));
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleJoinGame(authData, "BLACK", gameID));
    }

    @Test
    @Order(5)
    @DisplayName("Empty playerColor")
    public void emptyPlayerColor() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(authData, "", gameID));
    }

    @Test
    @Order(6)
    @DisplayName("Null playerColor")
    public void nullPlayerColor() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(authData, "", gameID));
    }

    @Test
    @Order(7)
    @DisplayName("Invalid playerColor")
    public void invalidPlayerColor() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(authData, "GREEN", gameID));
    }

    @Test
    @Order(8)
    @DisplayName("Lowercase playerColor")
    public void lowercasePlayerColor() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(authData, "black", gameID));
    }

    @Test
    @Order(9)
    @DisplayName("Incorrect gameID")
    public void incorrectGameID() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(authData, "BLACK", 8675309));
    }

    @Test
    @Order(10)
    @DisplayName("Negative gameID")
    public void negativeGameID() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(authData, "BLACK", -12));
    }

    @Test
    @Order(11)
    @DisplayName("Very large gameID")
    public void veryLargeGameID() {
        int veryLarge = Double.valueOf(Math.pow(10, 100)).intValue();
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleJoinGame(authData, "BLACK", veryLarge));
    }

    @Test
    @Order(11)
    @DisplayName("Join in occupied spot")
    public void alreadyTaken() {
        Assertions.assertDoesNotThrow(() -> handler.handleJoinGame(authData, "BLACK", gameID));
        Assertions.assertThrowsExactly(AlreadyTakenException.class, () -> handler.handleJoinGame(secondAuthData, "BLACK", gameID));
    }

    @Test
    @Order(12)
    @DisplayName("Same user in both spots")
    public void sameUserInBothSpots() {
        Assertions.assertDoesNotThrow(() -> handler.handleJoinGame(authData, "BLACK", gameID));
        Assertions.assertDoesNotThrow(() -> handler.handleJoinGame(secondAuthData, "WHITE", gameID));
    }

    @Test
    @Order(12)
    @DisplayName("One user in each spot")
    public void oneUserInEachSpot() {
        Assertions.assertDoesNotThrow(() -> handler.handleJoinGame(authData, "BLACK", gameID));
        Assertions.assertDoesNotThrow(() -> handler.handleJoinGame(secondAuthData, "WHITE", gameID));
    }

}
