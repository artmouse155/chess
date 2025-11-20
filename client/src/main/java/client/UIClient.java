package client;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class UIClient extends Client {

    private final UIHandler uiHandler;

    private static final String RESET_ALL = RESET_BG_COLOR + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT;
    private static final String APP_TITLE_FORMAT = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK;
    private static final String USERNAME_FORMAT = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE;

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
            case AUTHENTICATED -> String.format("%s ♕ 240 Chess ♕ %s %s %s> ", APP_TITLE_FORMAT, USERNAME_FORMAT, uiHandler.getUsername(), RESET_ALL);
            case UNAUTHENTICATED -> String.format("%s ♕ 240 Chess ♕ %s> ", APP_TITLE_FORMAT, RESET_ALL);
        };
        System.out.print(prompt);
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
            Handler.TerminalFunction terminalFunction = switch (uiHandler.getAuthState()) {
                case AUTHENTICATED -> switch (cmd) {
                    case "logout" -> uiHandler::logout;
                    case "create" -> uiHandler::createGame;
                    case "list" -> uiHandler::listGame;
                    case "play" -> uiHandler::playGame;
                    case "watch" -> uiHandler::observeGame;
                    case "" -> (String... p) -> "";
                    default -> uiHandler::help;
                };
                case UNAUTHENTICATED -> switch (cmd) {
                    case "login" -> uiHandler::login;
                    case "register" -> uiHandler::register;
                    case "quit" -> uiHandler::quit;
                    case "" -> (String... p) -> "";
                    default -> uiHandler::help;
                };
            };

            return terminalFunction.evaluate(params);
        } catch (ClientException ex) {
            return formatError(ex);
        }
    }
}
