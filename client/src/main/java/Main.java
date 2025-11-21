import client.repl.UIREPL;

public class Main {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        String wsUrl = "ws://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new UIREPL(serverUrl, wsUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}