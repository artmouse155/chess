package client;

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

    public String getHelp() {
        return help;
    }
}
