package client;

import handler.exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private final UserData testUser = new UserData("clientName", "securePWD123", "mail@gmail.com");

    private static Server server;
    private static ServerFacade serverFacade;

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

}
