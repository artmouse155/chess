package client;

public class Handler {

    private final ServerFacade server;

    public Handler(String url) {
        server = new ServerFacade(url);
    }
}
