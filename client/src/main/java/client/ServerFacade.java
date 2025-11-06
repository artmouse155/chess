package client;

import java.net.http.HttpClient;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final String authToken;

    private AuthState authState;
    private String username;

    public AuthState getAuthState() {
        return authState;
    }

    public String getUsername() {
        return username;
    }

    public enum AuthState {
        AUTHENTICATED,
        UNAUTHENTICATED
    }

    public ServerFacade(String url) {
        serverUrl = url;
        authToken = "";
        authState = AuthState.UNAUTHENTICATED;
    }

}
