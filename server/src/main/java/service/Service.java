package service;

import java.util.List;
import java.util.Map;

public class Service {

    public Service() {}

    public Map<String, String> deleteDB() {
        return Map.of();
    }

    public Map<String, String> register(String username, String password, String email) {
        return Map.of();
    }

    public Map<String, String> login(String username, String password) {
        return Map.of();
    }

    public Map<String, String> logout(String authToken) {
        return Map.of();
    }

    public Map<String, List<Map<String, Object>>> getGames() {
        return Map.of();
    }

    public Map<String, Number> createGame(String gameName) {
        return Map.of();
    }

    public Map<String, String> joinGame(String playerColor, int gameID) {
        return Map.of();
    }

}