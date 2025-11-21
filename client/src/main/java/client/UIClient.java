package client;

import java.util.Collection;
import java.util.Set;

public class UIClient extends Client {

    private final UIHandler uiHandler;

    private static final Collection<String> TERMINAL_STRINGS = Set.of("quit");

    public UIClient(String serverUrl, String wsUrl) {
        uiHandler = new UIHandler(serverUrl, wsUrl);

    }

    @Override
    protected Collection<String> getTerminalStrings() {
        return TERMINAL_STRINGS;
    }

    @Override
    protected String getIntroMessage() {
        return String.format("Welcome to Chess!%n%s", uiHandler.help());
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
    protected Handler.TerminalFunction getTerminalCommand(String cmd) {
        return switch (uiHandler.getAuthState()) {
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

    }
}
