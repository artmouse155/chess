package model;

import websocket.messages.ServerMessage;

import java.util.HashSet;
import java.util.Set;

public class GameConnectionPool {

    public enum BroadcastType {
        ALL,
        ONLY_OTHERS
    }

    private GameParticipant whitePlayer = null;
    private GameParticipant blackPlayer = null;
    private Set<GameParticipant> observers = new HashSet<>();

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

    public void sendMessage(ServerMessage message, BroadcastType type, String senderUsername) {

        if (whitePlayer != null && ((type == BroadcastType.ALL) || !whitePlayer.username().equals(senderUsername))) {
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
