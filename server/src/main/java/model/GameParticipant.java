package model;

import websocket.messages.ServerMessage;

public record GameParticipant(String username) {

    public void sendMessage(ServerMessage message) {
        System.out.printf("🌐 [%s] was sent %s%n", username, message.getServerMessageType());
    }
}
