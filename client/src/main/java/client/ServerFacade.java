package client;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class ServerFacade {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken;

    private AuthState authState;
    private String username;

    public enum AuthState {
        AUTHENTICATED,
        UNAUTHENTICATED
    }

    public ServerFacade(String url) {
        serverUrl = url;
        authToken = "";
        authState = AuthState.UNAUTHENTICATED;
    }

    public AuthState getAuthState() {
        return authState;
    }

    public String getUsername() {
        return username;
    }

    public PetList listPets() throws ResponseException {
        var request = buildRequest("GET", "/pet", null);
        var response = sendRequest(request);
        return handleResponse(response, PetList.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
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

}
