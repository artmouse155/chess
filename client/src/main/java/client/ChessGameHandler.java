package client;

import client.websocket.WebSocketFacade;

import java.util.function.Consumer;

public class ChessGameHandler extends Handler {

    private final WebSocketFacade webSocketFacade;

    public ChessGameHandler(String url, String authToken, Consumer<String> onWebSocketMessage) throws Exception {
        webSocketFacade = new WebSocketFacade(url, authToken, onWebSocketMessage);
    }

    @Override
    public String help(String... params) {
        return "WS HELP\n";
    }

    public String makeMove(String... params) {
        return "makeMove\n";
    }

    public String highlight(String... params) {
        return "highlight\n";
    }

    public String redraw(String... params) {
        return "redraw\n";
    }

    public String resign(String... params) {
        return "resign\n";
    }

    public String leave(String... params) throws ClientException {
        webSocketFacade.close();
        return "leave";
    }

    public String echo(String... params) throws ClientException {
        validateArgs(params, "echo <msg>\n", STRING);
        webSocketFacade.send(params[0]);
        return "Message sent\n";
    }
}
