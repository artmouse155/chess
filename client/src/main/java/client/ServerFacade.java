package client;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class ServerFacade {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverUrl;
    private AuthData authData;

    private AuthState authState;
    private GamesSet gamesSet = null;

    public enum AuthState {
        AUTHENTICATED,
        UNAUTHENTICATED
    }

    public ServerFacade(String url) {
        serverUrl = url;
        authData = new AuthData("", "");
        authState = AuthState.UNAUTHENTICATED;
    }

    public ServerFacade(int port) {
        this(String.format("http://localhost:%d", port));
    }

    public AuthState getAuthState() {
        return authState;
    }

    public String getUsername() {
        return authData.username();
    }

    public void register(UserData userData) throws ClientException {
        var request = buildRequest("POST", "/user", userData);
        var response = sendRequest(request);
        authData = handleResponse(response, AuthData.class);
        authState = AuthState.AUTHENTICATED;
    }

    public void login(LoginRequest loginRequest) throws ClientException {
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        authData = handleResponse(response, AuthData.class);
        authState = AuthState.AUTHENTICATED;
    }

    public void logout() throws ClientException {
        var request = buildRequest("DELETE", "/session", null);
        var response = sendRequest(request);
        handleResponse(response, EmptyResponse.class);
        authState = AuthState.UNAUTHENTICATED;
    }

    public GamesSet listGames() throws ClientException {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        gamesSet = handleResponse(response, GamesSet.class);
        return gamesSet;
    }

    public void createGame(CreateGameRequest createGameRequest) throws ClientException {
        var request = buildRequest("POST", "/game", createGameRequest);
        var response = sendRequest(request);
        handleResponse(response, CreateGameResponse.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws ClientException {
        var request = buildRequest("PUT", "/game", joinGameRequest);
        var response = sendRequest(request);
        handleResponse(response, EmptyResponse.class);
    }

    public ChessGameClient newChessGameClient(ChessGameClient.JoinType joinType, int gameID) throws ClientException {
        return new ChessGameClient(joinType, authData, gameID);
    }

    public GamesSet getCachedGamesSet() {
        return gamesSet;
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (authState == AuthState.AUTHENTICATED) {
            request.setHeader("authorization", authData.authToken());
        }
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ClientException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ClientException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ClientException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new ClientException(body);
            }

            throw new ClientException(Integer.toString(status));
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
