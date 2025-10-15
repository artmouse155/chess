package service;

import handler.exception.BadRequestException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class RegisterTests extends EndpointTests {

    @Test
    @Order(1)
    @DisplayName("Empty username")
    public void emptyUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class,
                () -> handler.handleRegister(new UserData("", testUser.password(), testUser.email()))
        );
    }

    @Test
    @Order(2)
    @DisplayName("Empty password")
    public void emptyPassword() {
        Assertions.assertThrowsExactly(BadRequestException.class,
                () -> handler.handleRegister(new UserData(testUser.username(), "", testUser.email()))
        );
    }

    @Test
    @Order(3)
    @DisplayName("Empty email")
    public void emptyEmail() {
        Assertions.assertThrowsExactly(BadRequestException.class,
                () -> handler.handleRegister(new UserData(testUser.username(), testUser.password(), ""))
        );
    }

    @Test
    @Order(4)
    @DisplayName("Null username")
    public void nullUsername() {
        Assertions.assertThrowsExactly(BadRequestException.class,
                () -> handler.handleRegister(new UserData(null, testUser.password(), testUser.email()))
        );
    }

    @Test
    @Order(5)
    @DisplayName("Null password")
    public void nullPassword() {
        Assertions.assertThrowsExactly(BadRequestException.class,
                () -> handler.handleRegister(new UserData(testUser.username(), null, testUser.email()))
        );
    }

    @Test
    @Order(6)
    @DisplayName("Null email")
    public void nullEmail() {
        Assertions.assertThrowsExactly(BadRequestException.class,
                () -> handler.handleRegister(new UserData(testUser.username(), testUser.password(), null))
        );
    }

    @Test
    @Order(7)
    @DisplayName("Successful Register")
    public void successfulRegister() {
        Assertions.assertDoesNotThrow(() -> handler.handleRegister(testUser));
    }
}
