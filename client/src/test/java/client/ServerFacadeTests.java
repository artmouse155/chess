package client;

import chess.ChessGame;
import client.websocket.ChessGameREPL;
import handler.exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private final UserData testUser = new UserData("clientName", "securePWD123", "mail@gmail.com");
    private final LoginRequest testLoginRequest;
    private final String testGameName = "testGame";

    private static Server server;
    private static ServerFacade serverFacade;

    public ServerFacadeTests() {
        testLoginRequest = new LoginRequest(testUser.username(), testUser.password());
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
        register();
        Assertions.assertDoesNotThrow(() -> serverFacade.login(testLoginRequest));
    }

    @Test
    public void loginNegative() {
        Assertions.assertThrowsExactly(ClientException.class, () -> serverFacade.login(testLoginRequest));
    }

    @Test
    public void logoutPositive() {
        register();
        Assertions.assertDoesNotThrow(() -> serverFacade.logout());
    }

    @Test
    public void logoutNegative() {
        Assertions.assertThrowsExactly(ClientException.class, () -> serverFacade.logout());
    }

    @Test
    public void listGamesPositive() {
        register();
        Assertions.assertDoesNotThrow(() -> serverFacade.listGames());
    }

    @Test
    public void listGamesNegative() {
        Assertions.assertThrowsExactly(ClientException.class, () -> serverFacade.listGames());
    }

    @Test
    public void createGamePositive() {
        register();
        Assertions.assertDoesNotThrow(() -> serverFacade.createGame(new CreateGameRequest(testGameName)));
    }

    @Test
    public void createGameNegativeInput() {
        register();
        Assertions.assertThrowsExactly(ClientException.class, () -> serverFacade.createGame(null));
    }

    @Test
    public void createGameNegativeAuth() {
        Assertions.assertThrowsExactly(ClientException.class, () -> serverFacade.createGame(new CreateGameRequest(testGameName)));
    }

    @Test
    public void joinGamePositive() {
        register();
        Assertions.assertDoesNotThrow(() -> serverFacade.createGame(new CreateGameRequest(testGameName)));
        var gamesSet = Assertions.assertDoesNotThrow(() -> serverFacade.listGames());
        GameDataStripped game = Assertions.assertDoesNotThrow(() -> gamesSet.games().stream().findFirst().orElseThrow(AssertionError::new));
        int gameID = game.gameID();
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(new JoinGameRequest(
                ChessGame.TeamColor.WHITE, gameID
        )));
    }

    @Test
    public void joinGameNegative() {
        register();
        var gamesSet = Assertions.assertDoesNotThrow(() -> serverFacade.listGames());
        Assertions.assertTrue(gamesSet.games().isEmpty());
        Assertions.assertThrowsExactly(ClientException.class, () -> serverFacade.joinGame(new JoinGameRequest(
                ChessGame.TeamColor.WHITE, 1234
        )));
    }

    @Test
    public void newChessGameClientPositive() {
        Assertions.assertDoesNotThrow(() -> serverFacade.newChessGameClient(testUser.username(), testGameName, ChessGameREPL.JoinType.OBSERVER,
                1));
    }

    @Test
    public void newChessGameClientNegative() throws ClientException {
        Assertions.assertThrowsExactly(ClientException.class, () -> serverFacade.newChessGameClient(null, "Game Name",
                ChessGameREPL.JoinType.BLACK, 1));
    }

}
