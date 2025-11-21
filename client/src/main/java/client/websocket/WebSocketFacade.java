package client.websocket;

import client.ClientException;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

public class WebSocketFacade extends Endpoint {
    public final Session session;
    private final String authtoken;
    private final int gameID;

    public WebSocketFacade(String url, String authtoken, int gameID, Consumer<String> onMessage) throws Exception {
        URI uri = new URI(url);
        this.authtoken = authtoken;
        this.gameID = gameID;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String s) {
                onMessage.accept(s);
            }
        });
    }

    public void send(String message) throws ClientException {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new ClientException(String.format("Websocket Error: %s", e.getMessage()));
        }
    }

    public void sendCommand(UserGameCommand.CommandType type, String body) throws ClientException {
        send(new UserGameCommand(type, authtoken, gameID, body).toString());
    }

    public void sendCommand(UserGameCommand.CommandType type) throws ClientException {
        send(new UserGameCommand(type, authtoken, gameID).toString());
    }

    // This method must be overridden, but we don't have to do anything with it
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void close() throws ClientException {
        try {
            session.close();
        } catch (IOException e) {
            throw new ClientException(String.format("Websocket Error: %s", e.getMessage()));
        }
    }
}
