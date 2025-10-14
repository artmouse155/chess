package service;

import handler.exception.BadRequestException;
import handler.exception.UnauthorizedException;
import org.junit.jupiter.api.*;

public class LoginTests extends EndpointTests {

    @BeforeEach
    public void beforeEach() {
        registerTestUser();
    }

    @Test
    @Order(1)
    @DisplayName("Empty username")
    public void emptyUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleLogin("", testUser.password()));
    }

    @Test
    @Order(2)
    @DisplayName("Empty password")
    public void emptyPassword() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleLogin(testUser.username(), ""));
    }

    @Test
    @Order(4)
    @DisplayName("Null username")
    public void nullUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleLogin(null, testUser.password()));
    }

    @Test
    @Order(5)
    @DisplayName("Null password")
    public void nullPassword() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleLogin(testUser.username(), null));
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
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleLogin(testUser.username(), "iForgotMyPassword"));
    }

    @Test
    @Order(7)
    @DisplayName("Correct username and password")
    public void correctUsernameCorrectPassword() {
        loginTestUser();
    }
}
