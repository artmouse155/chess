package model;

import websocket.messages.ServerMessage;

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
        return whitePlayer.username();
    }

    public String blackUsername() {
        return blackPlayer.username();
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

    public void sendMessage(ServerMessage message, BroadcastType type, String senderUsername) {

        if (
                whitePlayer != null &&
                        (
                                (type == BroadcastType.ALL) ||
                                        !whitePlayer.username().equals(senderUsername))) {
            whitePlayer.sendMessage(message);
        }

        if (blackPlayer != null && ((type == BroadcastType.ALL) || !blackPlayer.username().equals(senderUsername))) {
            blackPlayer.sendMessage(message);
        }

        for (final var participant : observers) {
            if (participant != null && ((type == BroadcastType.ALL) || !participant.username().equals(senderUsername))) {
                participant.sendMessage(message);
            }
        }
    }
}
