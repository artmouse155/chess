package client;

import com.google.gson.Gson;
import model.UserData;

import java.util.Map;

public class ClientException extends Exception {

    private final String help;

    public ClientException(String message) {
        super(message);
        help = "";
    }

    public ClientException(String message, String help) {
        super(message);
        this.help = help;
    }

    public static ClientException parseHTTPError(int code) {
        String message = switch (code) {
            case 400 -> "Bad request. Check your input formatting.\n";
            case 401 -> "Unauthorized\n";
            case 403 -> "Already taken.\n";
            case 500 -> "The server encountered an unexpected error. Try again later.\n";
            default -> "An unexpected error occurred.\n";
        };
        return new ClientException(message);
    }

    public String getHelp() {
        return (help == "") ? "" : "Expected: " + help;
    }
}
