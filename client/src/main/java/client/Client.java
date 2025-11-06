package client;

import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private final Handler handler;
    private final AuthState authState;

    enum AuthState {
        AUTHENTICATED,
        UNAUTHENTICATED
    }

    interface TerminalFunction {

        String evaluate(String... params);
    }

    public Client(String serverUrl) {
        handler = new Handler(serverUrl);

    }

    public void run() {
        System.out.println("Welcome to Chess.");
        System.out.print(handler.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            result = eval(line);
            System.out.print(result);
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("CS 240 > ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            TerminalFunction terminalFunction = switch (authState) {
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

            return terminalFunction.evaluate();
        } catch (ClientException ex) {
            return ex.getMessage();
        }
    }
}
