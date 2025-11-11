package client;

import java.util.Arrays;
import java.util.Scanner;

public class UIClient extends Client {

    private final UIHandler UIHandler;


    interface TerminalFunction {

        String evaluate(String... params) throws ClientException;
    }

    public UIClient(String serverUrl) {
        UIHandler = new UIHandler(serverUrl);

    }

    @Override
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

    @Override
    protected void printPrompt() {

        String prompt = switch (UIHandler.getAuthState()) {
            case AUTHENTICATED -> String.format("[%s] CS 240 > ", UIHandler.getUsername());
            case UNAUTHENTICATED -> "CS 240 > ";
        };
        System.out.print(prompt);
    }

    @Override
    protected String formatError(ClientException ex) {
        return String.format("%s%s", ex.getMessage(), ex.getHelp());
    }


    @Override
    protected String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" +");
            String cmd;
            String[] params;
            if ((tokens.length > 0)) {
                cmd = tokens[0];
                params = Arrays.copyOfRange(tokens, 1, tokens.length);
            } else {
                cmd = "";
                params = new String[]{};
            }
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
