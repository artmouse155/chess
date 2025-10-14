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
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleRegister(new UserData("", "soSecure!!!", "test@gmail.com")));
    }

    @Test
    @Order(2)
    @DisplayName("Empty password")
    public void emptyPassword() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleRegister(new UserData("test1234", "", "test@gmail.com")));
    }

    @Test
    @Order(3)
    @DisplayName("Empty email")
    public void emptyEmail() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> handler.handleRegister(new UserData("test1234", "soSecure!!!", "")));
    }


}
