package client;

import client.websocket.WsEchoClient;

import java.util.Scanner;

public class ChessGameHandler {

    private final WsEchoClient wsEchoClient;

    public ChessGameHandler(String url, String authToken) throws Exception {
        wsEchoClient = new WsEchoClient(url, authToken);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a message you want to echo:");
        while (true) {
            wsEchoClient.send(scanner.nextLine());
        }
    }
}
