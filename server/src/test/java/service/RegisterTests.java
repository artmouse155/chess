package service;

import handler.BadRequestException;
import handler.UnauthorizedException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class RegisterTests extends EndpointTests {

    @Test
    @Order(1)
    @DisplayName("Correct username, invalid password")
    public void correctUsernameInvalidPassword() {
        Assertions.assertThrowsExactly(BadRequestException.class, () -> service.register(new UserData("test1234", "", "email@gmail.com")));
    }

}
