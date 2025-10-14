package service;

import handler.exception.BadRequestException;
import handler.exception.UnauthorizedException;
import org.junit.jupiter.api.*;

public class AuthTests extends EndpointTests {

    @BeforeEach
    public void beforeEach() {
        registerTestUser();
    }

    @Test
    @Order(1)
    @DisplayName("Empty authToken")
    public void emptyUsername() {
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleAuth(""));
    }

    @Test
    @Order(2)
    @DisplayName("Null authToken")
    public void missingAuthToken() {
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleAuth(null));
    }

    @Test
    @Order(3)
    @DisplayName("Wrong authToken")
    public void wrongAuthToken() {
        loginTestUser();
        Assertions.assertThrowsExactly(UnauthorizedException.class, () -> handler.handleAuth("invalidAuthToken"));
    }

    @Test
    @Order(3)
    @DisplayName("Wrong authToken")
    public void correctAuthToken() {
        var authData = loginTestUser();
        Assertions.assertDoesNotThrow(() -> handler.handleAuth(authData.authToken()));
    }
}
