package client;

import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private final UIHandler UIHandler;


    interface TerminalFunction {

        String evaluate(String... params) throws ClientException;
    }

    public Client(String serverUrl) {
        UIHandler = new UIHandler(serverUrl);

    }

    public void run() {
        System.out.println("Welcome to Chess.");
        System.out.print(UIHandler.help());

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

        String prompt = switch (UIHandler.getAuthState()) {
            case AUTHENTICATED -> String.format("[%s] CS 240 > ", UIHandler.getUsername());
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
            TerminalFunction terminalFunction = switch (UIHandler.getAuthState()) {
                case AUTHENTICATED -> switch (cmd) {
                    case "logout" -> UIHandler::logout;
                    case "create" -> UIHandler::createGame;
                    case "list" -> UIHandler::listGame;
                    case "play" -> UIHandler::playGame;
                    case "watch" -> UIHandler::observeGame;
                    default -> UIHandler::help;
                };
                case UNAUTHENTICATED -> switch (cmd) {
                    case "login" -> UIHandler::login;
                    case "register" -> UIHandler::register;
                    case "quit" -> UIHandler::quit;
                    default -> UIHandler::help;
                };
            };

            return terminalFunction.evaluate(params);
        } catch (ClientException ex) {
            return formatError(ex);
        }
    }
}
