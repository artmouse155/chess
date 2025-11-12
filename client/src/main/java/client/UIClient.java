package client;

import java.util.Arrays;
import java.util.Scanner;

public class UIClient extends Client {

    private final UIHandler uiHandler;


    interface TerminalFunction {

        String evaluate(String... params) throws ClientException;
    }

    public UIClient(String serverUrl) {
        uiHandler = new UIHandler(serverUrl);

    }

    @Override
    public void run() {
        System.out.println("Welcome to Chess!");
        System.out.print(uiHandler.help());

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

        String prompt = switch (uiHandler.getAuthState()) {
            case AUTHENTICATED -> String.format("[%s] CS 240 > ", uiHandler.getUsername());
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
            String[] tokens = input.split(" +");
            String cmd;
            String[] params;
            if ((tokens.length > 0)) {
                cmd = tokens[0].toLowerCase();
                params = Arrays.copyOfRange(tokens, 1, tokens.length);
            } else {
                cmd = "";
                params = new String[]{};
            }
            TerminalFunction terminalFunction = switch (uiHandler.getAuthState()) {
                case AUTHENTICATED -> switch (cmd) {
                    case "logout" -> uiHandler::logout;
                    case "create" -> uiHandler::createGame;
                    case "list" -> uiHandler::listGame;
                    case "play" -> uiHandler::playGame;
                    case "watch" -> uiHandler::observeGame;
                    default -> uiHandler::help;
                };
                case UNAUTHENTICATED -> switch (cmd) {
                    case "login" -> uiHandler::login;
                    case "register" -> uiHandler::register;
                    case "quit" -> uiHandler::quit;
                    default -> uiHandler::help;
                };
            };

            return terminalFunction.evaluate(params);
        } catch (ClientException ex) {
            return formatError(ex);
        }
    }
}
