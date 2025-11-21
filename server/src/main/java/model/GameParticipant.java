package model;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public record GameParticipant(Session session, String username) {

    public void sendMessage(ServerMessage message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(message.toString());
            String innerMessage = "";
            if (message instanceof ErrorMessage) {
                innerMessage = ": " + ((ErrorMessage) message).getErrorMessage();
            } else if (message instanceof NotificationMessage) {
                innerMessage = ": " + ((NotificationMessage) message).getMessage();
            }
            System.out.printf("🌐 [%s] was sent %s%s%n", username, message.getServerMessageType(), innerMessage);
        }
    }

    public boolean isClosed() {
        return !session.isOpen();
    }
}
