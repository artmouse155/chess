package client.websocket;

import client.ClientException;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

public class WsEchoClient extends Endpoint {
    public Session session;

    public WsEchoClient(String url, String authtoken, Consumer<String> onMessage) throws Exception {
        URI uri = new URI(url);
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

    // This method must be overridden, but we don't have to do anything with it
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
