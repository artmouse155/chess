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
    private String authToken;

    private AuthState authState;
    private String username;
    private GamesList gamesList = new GamesList(Set.of());

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

    public String getUsername() {
        return username;
    }

    public void register(UserData userData) throws ClientException {
        var request = buildRequest("POST", "/user", userData);
        var response = sendRequest(request);
        AuthData authData = handleResponse(response, AuthData.class);
        username = authData.username();
        authToken = authData.authToken();
        authState = AuthState.AUTHENTICATED;
    }

    public void login(RegisterRequest registerRequest) throws ClientException {
        var request = buildRequest("POST", "/session", registerRequest);
        var response = sendRequest(request);
        AuthData authData = handleResponse(response, AuthData.class);
        username = authData.username();
        authToken = authData.authToken();
        authState = AuthState.AUTHENTICATED;
    }

    public void logout() throws ClientException {
        var request = buildRequest("DELETE", "/session", null);
        var response = sendRequest(request);
        handleResponse(response, EmptyResponse.class);
        authState = AuthState.UNAUTHENTICATED;
    }

    public GamesList listGames() throws ClientException {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        gamesList = handleResponse(response, GamesList.class);
        return gamesList.simplyNumbered();
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
