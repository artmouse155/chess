package handler;

import java.util.List;
import java.util.Map;

public class Handler {
    public Handler() {}

    public void deleteDB() {
    }

    public Map<String, String> handleRegister(String username, String password, String email) {
        return Map.of();
    }

    public Map<String, String> handleLogin(String username, String password) {
        return Map.of();
    }

    public Map<String, String> handleLogout(String authToken) {
        return Map.of();
    }

    public Map<String, List<Map<String, Object>>> handleGetGames() {
        return Map.of();
    }

    public Map<String, Number> handleCreateGame(String gameName) {
        return Map.of();
    }

    public Map<String, String> handleJoinGame(String playerColor, int gameID) {
        return Map.of();
    }
}