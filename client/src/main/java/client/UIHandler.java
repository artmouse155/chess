package client;

import model.*;

public class UIHandler extends Handler {

    private final ChessGameClient chessGameClient;

    private final String authHelp = """
            list                     | List current chess games
            join <game id> <b|w>     | Join a game as black or white
            watch <game id>          | Observe a game
            create <name>            | Create new chess game with specified name
            logout                   | Log out
            """;

    private final String unauthHelp = """
            login <username> <password>             | Log in existing user
            register <email> <username> <password>  | Create new user
            quit                                    | Quit application
            """;

    private final String PLAYER_COLOR = "b|w|black|white";

    public UIHandler(String url) {
        super(url);
        chessGameClient = new ChessGameClient();
    }

    public String help(String... params) {
        String helpString = switch (server.getAuthState()) {
            case AUTHENTICATED -> authHelp;
            case UNAUTHENTICATED -> unauthHelp;
        };
        return helpString;
    }

    public String quit(String... params) {
        return "quit";
    }

    public String login(String... params) throws ClientException {
        validateArgs(params, "login <username> <password>\n", STRING, STRING);
        LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
        server.login(loginRequest);
        return String.format("Login successful. Welcome, %s!\n", server.getUsername());
    }

    public String register(String... params) throws ClientException {
        validateArgs(params, "register <email> <username> <password>\n", STRING, STRING, STRING);
        UserData request = new UserData(params[1], params[2], params[0]);
        server.register(request);
        return String.format("Registration successful. Welcome, %s!\n", server.getUsername());
    }

    public String logout(String... params) throws ClientException {
        validateArgs(params, "logout\n");
        server.logout();
        return "Logout successful.";
    }

    public String listGame(String... params) throws ClientException {
        validateArgs(params, "list\n");
        GamesSet gamesSet = server.listGames();

        String result = "";

        if (gamesSet.games().isEmpty()) {
            result += "No games to display.\n";
        } else {
            result += String.format("%d game(s) found:\n", gamesSet.games().size());
            for (final var game : gamesSet.simplyNumbered().games()) {
                result += String.format(
                        "%d. NAME: %s WHITE: %s BLACK: %s\n",
                        game.gameID(),
                        game.gameName(),
                        game.whiteUsername(),
                        game.blackUsername()
                );
            }
        }

        return result + "\n";
    }

    public String playGame(String... params) throws ClientException {
        validateArgs(params, "join <game id> <b|w>", POSITIVE_INTEGER, PLAYER_COLOR);
        return "CHESS PLAY!!!!!!! (type anything to quit)\n";
    }

    public String observeGame(String... params) throws ClientException {
        return "observeGame\n";
    }

    public String createGame(String... params) throws ClientException {
        validateArgs(params, "create <name>\n", STRING);
        CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
        server.createGame(createGameRequest);
        return String.format("Game %s created. Use the \"list\" command to show a list of all games.\n", params[0]);
    }
}
