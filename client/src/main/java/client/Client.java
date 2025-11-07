package client;

import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private final Handler handler;


    interface TerminalFunction {

        String evaluate(String... params) throws ClientException;
    }

    public Client(String serverUrl) {
        handler = new Handler(serverUrl);

    }

    public void run() {
        System.out.println("Welcome to Chess.");
        System.out.print(handler.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            result = eval(line);
            if (result.equals("quit")) {
                return;
            }

            System.out.print(result);
        }
    }

    private void printPrompt() {

        String prompt = switch (handler.getAuthState()) {
            case AUTHENTICATED -> "[CHASE] CS 240 > ";
            case UNAUTHENTICATED -> "CS 240 > ";
        };
        System.out.print(prompt);
    }

    private String formatError(ClientException ex) {
        return String.format("%s%s", ex.getMessage(), ex.getHelp());
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            TerminalFunction terminalFunction = switch (handler.getAuthState()) {
                case AUTHENTICATED -> switch (cmd) {
                    case "logout" -> handler::logout;
                    case "create" -> handler::createGame;
                    case "list" -> handler::listGame;
                    case "play" -> handler::playGame;
                    case "watch" -> handler::observeGame;
                    default -> handler::help;
                };
                case UNAUTHENTICATED -> switch (cmd) {
                    case "login" -> handler::login;
                    case "register" -> handler::register;
                    case "quit" -> handler::quit;
                    default -> handler::help;
                };
            };

            return terminalFunction.evaluate(params);
        } catch (ClientException ex) {
            return formatError(ex);
        }
    }
}
