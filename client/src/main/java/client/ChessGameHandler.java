package client;

import client.websocket.WsEchoClient;

import java.util.Scanner;

public class ChessGameHandler extends Handler {

    private final WsEchoClient wsEchoClient;

    public ChessGameHandler(String url, String authToken) throws Exception {
        wsEchoClient = new WsEchoClient(url, authToken);
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

    public String leave(String... params) {
        return "leave\n";
    }

    public String echo(String... params) throws ClientException {
        validateArgs(params, "echo <msg>\n", STRING);
        wsEchoClient.send(params[0]);
        return "Message sent\n";
    }
}
