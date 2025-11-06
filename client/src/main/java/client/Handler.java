package client;

public class Handler {

    private final ServerFacade server;

    public Handler(String url) {
        server = new ServerFacade(url);
    }

    public String help(String... params) {
        return "help";
    }

    public String quit(String... params) {
        return "quit";
    }

    public String login(String... params) {
        return "login";
    }

    public String register(String... params) {
        return "register";
    }

    public String logout(String... params) {
        return "logout";
    }

    public String createGame(String... params) {
        return "createGame";
    }

    public String listGame(String... params) {
        return "listGame";
    }

    public String playGame(String... params) {
        return "playGame";
    }

    public String observeGame(String... params) {
        return "observeGame";
    }

    public ServerFacade.AuthState getAuthState() {
        return server.getAuthState();
    }

    public String getUsername() {
        return server.getUsername();
    }
}
