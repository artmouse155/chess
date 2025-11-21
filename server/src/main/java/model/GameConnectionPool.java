package model;

import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GameConnectionPool {

    public enum BroadcastType {
        ALL,
        ONLY_OTHERS,
        ONLY_SELF
    }

    private GameParticipant whitePlayer = null;
    private GameParticipant blackPlayer = null;
    private Set<GameParticipant> observers = new HashSet<>();

    public String whiteUsername() {
        return (whitePlayer == null) ? null : whitePlayer.username();
    }

    public String blackUsername() {
        return (blackPlayer == null) ? null : blackPlayer.username();
    }

    public void setWhitePlayer(GameParticipant participant) {
        whitePlayer = participant;
    }

    public void setBlackPlayer(GameParticipant participant) {
        blackPlayer = participant;
    }

    public void removeWhitePlayer() {
        whitePlayer = null;
    }

    public void removeBlackPlayer() {
        blackPlayer = null;
    }

    public void addObserver(GameParticipant participant) {
        observers.add(participant);
    }

    public void removeObserver(String participantUsername) {
        observers.removeIf(participant -> (participant == null || participant.username().equals(participantUsername)));
    }

    public boolean isEmpty() {
        return (whitePlayer == null && blackPlayer == null && observers.isEmpty());
    }

    private boolean canSend(BroadcastType type, String senderUsername, GameParticipant reciever) {
        return (reciever != null) && switch (type) {
            case ALL -> true;
            case ONLY_OTHERS -> !reciever.username().equals(senderUsername);
            case ONLY_SELF -> reciever.username().equals(senderUsername);
        };
    }

    public void sendMessage(ServerMessage message, BroadcastType type, String senderUsername) throws IOException {

        if (canSend(type, senderUsername, whitePlayer)) {
            whitePlayer.sendMessage(message);
        }

        if (canSend(type, senderUsername, blackPlayer)) {
            blackPlayer.sendMessage(message);
        }

        for (final var participant : observers) {
            if (canSend(type, senderUsername, participant)) {
                participant.sendMessage(message);
            }
        }
    }
}
