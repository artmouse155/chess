package client;

import handler.exception.ResponseException;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private final UserData testUser = new UserData("clientName", "securePWD123", "mail@gmail.com");
    private final RegisterRequest testRegisterRequest;

    private static Server server;
    private static ServerFacade serverFacade;

    public ServerFacadeTests() {
        testRegisterRequest = new RegisterRequest(testUser.username(), testUser.password());
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDB() throws ResponseException {
        server.clear();
    }

    private void register() {
        Assertions.assertDoesNotThrow(() -> serverFacade.register(testUser));
    }

    private void register_and_login() {
        register();
        Assertions.assertDoesNotThrow(() -> serverFacade.login(testRegisterRequest));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerPositive() {
        Assertions.assertDoesNotThrow(() -> serverFacade.register(testUser));
    }

    @Test
    public void registerNegative() {
        Assertions.assertThrowsExactly(ClientException.class, () -> serverFacade.register(new UserData(null, null, null)));
    }

    @Test
    public void loginPositive() {
        register_and_login();
    }

    @Test
    public void loginNegative() {
        Assertions.assertThrowsExactly(ClientException.class, () -> serverFacade.login(testRegisterRequest));
    }

}
