package client;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken;

    private AuthState authState;
    private GamesList gamesList = null;

    public enum AuthState {
        AUTHENTICATED,
        UNAUTHENTICATED
    }

    public ServerFacade(String url) {
        serverUrl = url;
        authToken = "";
        authState = AuthState.UNAUTHENTICATED;
    }

    public ServerFacade(int port) {
        this(String.format("http://localhost:%d", port));
    }

    public AuthState getAuthState() {
        return authState;
    }

    public void register(UserData userData) throws ClientException {
        var request = buildRequest("POST", "/user", userData);
        var response = sendRequest(request);
        var authData = handleResponse(response, AuthData.class);
        authToken = authData.authToken();
        authState = AuthState.AUTHENTICATED;
    }

    public void login(LoginRequest loginRequest) throws ClientException {
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        var authData = handleResponse(response, AuthData.class);
        authToken = authData.authToken();
        authState = AuthState.AUTHENTICATED;
    }

    public void logout() throws ClientException {
        var request = buildRequest("DELETE", "/session", null);
        var response = sendRequest(request);
        handleResponse(response, EmptyResponse.class);
        authToken = "";
        authState = AuthState.UNAUTHENTICATED;
    }

    public GamesList listGames() throws ClientException {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        gamesList = handleResponse(response, GamesList.class);
        return gamesList;
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
        return new ChessGameClient(joinType, authToken, gameID);
    }

    public GamesList getCachedGamesSet() {
        return gamesList;
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (authState == AuthState.AUTHENTICATED) {
            request.setHeader("authorization", authToken);
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
//            var body = response.body();
//            if (body != null) {
//                throw ClientException.parseHTTPError(body);
//            }

            throw ClientException.parseHTTPError(status);
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
