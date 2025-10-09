package handler;

import service.Service;

import java.util.List;
import java.util.Map;

public class Handler {

    private final Service service;

    public Handler() {
        service = new Service();
    }

    public Map<String, String> handleDeleteDB() {
        return service.deleteDB();
    }

    public Map<String, String> handleRegister(String username, String password, String email) {
        return service.register(username, password, email);
    }

    public Map<String, String> handleLogin(String username, String password) {
        return service.login(username, password);
    }

    public Map<String, String> handleLogout(String authToken) {
        return service.logout(authToken);
    }

    public Map<String, List<Map<String, Object>>> handleGetGames() {
        return service.getGames();
    }

    public Map<String, Number> handleCreateGame(String gameName) {
        return service.createGame(gameName);
    }

    public Map<String, String> handleJoinGame(String playerColor, int gameID) {
        return service.joinGame(playerColor, gameID);
    }
}