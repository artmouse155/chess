package client;

import com.google.gson.Gson;
import model.AuthData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

    public AuthData register(String username, String password, String email) throws ClientException {
        var request = buildRequest("POST", "/user", null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
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

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
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
