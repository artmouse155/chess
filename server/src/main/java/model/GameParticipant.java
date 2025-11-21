package model;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public record GameParticipant(Session session, String username) {

    public void sendMessage(ServerMessage message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(message.toString());
        }
        System.out.printf("🌐 [%s] was sent %s%n", username, message.getServerMessageType());
    }
}
