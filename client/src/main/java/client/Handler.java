package client;

public class Handler {

    private final ServerFacade server;

    private final String authHelp = """
            authHelp
            """;

    private final String unauthHelp = """
            login <username> <password>
            register <email> <username> <password>
            quit
            """;

    public Handler(String url) {
        server = new ServerFacade(url);
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

    public String login(String... params) {
        return "login\n";
    }

    public String register(String... params) {
        return "register\n";
    }

    public String logout(String... params) {
        return "logout\n";
    }

    public String createGame(String... params) {
        return "createGame\n";
    }

    public String listGame(String... params) {
        return "listGame\n";
    }

    public String playGame(String... params) {
        return "playGame\n";
    }

    public String observeGame(String... params) {
        return "observeGame\n";
    }

    public ServerFacade.AuthState getAuthState() {
        return server.getAuthState();
    }

    public String getUsername() {
        return server.getUsername();
    }
}
