package client;

import chess.ChessGame;
import model.*;

public class UIHandler extends Handler {

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

    private final String PLAYER_COLOR = "B|W|BLACK|WHITE";

    public UIHandler(String url) {
        super(url);
    }

    public String help(String... _params) {
        return switch (server.getAuthState()) {
            case AUTHENTICATED -> authHelp;
            case UNAUTHENTICATED -> unauthHelp;
        };
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
        int relativeGameID = Integer.parseInt(params[0]);
        int gameID = getGameIDFromRelative(relativeGameID);
        ChessGame.TeamColor teamColor = switch (params[1].toUpperCase()) {
            case "B", "BLACK" -> ChessGame.TeamColor.BLACK;
            case "W", "WHITE" -> ChessGame.TeamColor.WHITE;
            default -> throw new ClientException("Unexpected value: " + params[1].toUpperCase());
        };
        server.joinGame(new JoinGameRequest(teamColor, gameID));

        ChessGameClient.JoinType joinType = switch (teamColor) {
            case WHITE -> ChessGameClient.JoinType.WHITE;
            case BLACK -> ChessGameClient.JoinType.BLACK;
        };

        var chessGameClient = server.newChessGameClient(joinType, gameID);
        chessGameClient.run();
        return "Chess game ended.\n";
    }

    public String observeGame(String... params) throws ClientException {
        validateArgs(params, "watch <game id>", POSITIVE_INTEGER);
        int relativeGameID = Integer.parseInt(params[0]);
        int gameID = getGameIDFromRelative(relativeGameID);
        var chessGameClient = server.newChessGameClient(ChessGameClient.JoinType.OBSERVER, gameID);
        chessGameClient.run();

        return "observeGame\n";
    }

    public String createGame(String... params) throws ClientException {
        validateArgs(params, "create <name>\n", STRING);
        CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
        server.createGame(createGameRequest);
        return String.format("Game %s created. Use the \"list\" command to show a list of all games.\n", params[0]);
    }

    private int getGameIDFromRelative(int relativeGameID) throws ClientException {
        var gamesSet = server.getCachedGamesSet();
        if (gamesSet == null) {
            throw new ClientException(
                    "Use the \"list\" command to show a list of all games."
            );
        } else {
            try {
                return gamesSet.gameIDAtIndex(relativeGameID);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ClientException(
                        "That number was not in the list."
                );
            }
        }
    }
}
