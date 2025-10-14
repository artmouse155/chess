package service;

import handler.exception.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.*;

public class LogoutTests extends EndpointTests {

    private AuthData authData;

    @BeforeEach
    public void beforeEach() {
        registerTestUser();
        authData = loginTestUser();
    }

    @Test
    @Order(1)
    @DisplayName("Double logout")
    public void doubleLogout() {
        Assertions.assertDoesNotThrow(() -> handler.handleLogout(authData.authToken()));
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleLogout(authData.authToken()));
    }

    @Test
    @Order(2)
    @DisplayName("Wrong authToken for logout")
    public void wrongAuthTokenForLogout() {
        loginTestUser();
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleLogout("invalidAuthToken"));
    }

    @Test
    @Order(3)
    @DisplayName("Use expired authToken")
    public void expiredAuthToken() {
        var authData = loginTestUser();
        Assertions.assertDoesNotThrow(() -> handler.handleLogout(authData.authToken()));
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleAuth(authData.authToken()));
    }


    @Test
    @Order(4)
    @DisplayName("Login and logout")
    public void correctAuthToken() {
        var authData = loginTestUser();
        Assertions.assertDoesNotThrow(() -> handler.handleLogout(authData.authToken()));
    }
}
