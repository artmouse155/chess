package service;

import handler.exception.BadRequestException;
import handler.exception.UnauthorizedException;
import model.UserData;
import org.junit.jupiter.api.*;

public class LoginTests extends EndpointTests {

    @BeforeEach
    public void register() {
        Assertions.assertDoesNotThrow(() -> handler.handleRegister(new UserData("test1234", "soSecure!!!", "test@gmail.com")));
    }

    @Test
    @Order(1)
    @DisplayName("Empty username")
    public void emptyUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleLogin("", "soSecure!!!"));
    }

    @Test
    @Order(2)
    @DisplayName("Empty password")
    public void emptyPassword() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleLogin("test1234", ""));
    }

    @Test
    @Order(4)
    @DisplayName("Null username")
    public void nullUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleLogin(null, "soSecure!!!"));
    }

    @Test
    @Order(5)
    @DisplayName("Null password")
    public void nullPassword() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleLogin("test1234", null));
    }

    @Test
    @Order(6)
    @DisplayName("Nonexistent username")
    public void nonexistentUsername() {
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleLogin("jamesGosling12345", "c++IsBetter"));
    }

    @Test
    @Order(7)
    @DisplayName("Correct username and incorrect password")
    public void correctUsernameIncorrectPassword() {
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleLogin("test1234", "iForgotMyPassword"));
    }

    @Test
    @Order(7)
    @DisplayName("Correct username and password")
    public void correctUsernameCorrectPassword() {
        Assertions.assertDoesNotThrow(() -> handler.handleLogin("test1234", "soSecure!!!"));
    }
}
