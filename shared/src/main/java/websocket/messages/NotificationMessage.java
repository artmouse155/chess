package websocket.messages;

public class NotificationMessage {

    private final String message;

    public NotificationMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
