package model;

import websocket.messages.ServerMessage;

public record GameParticipant(String username) {

    public void sendMessage(ServerMessage message) {
        System.out.printf("Sent message %s to %s", message.toString(), username);
    }
}
