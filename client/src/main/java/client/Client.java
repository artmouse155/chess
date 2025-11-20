package client;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public abstract class Client {

    private static final String ERROR_FORMAT = SET_TEXT_COLOR_RED;
    private static final String HELP_FORMAT = SET_TEXT_COLOR_BLUE;


    public abstract void run();

    protected String formatError(ClientException ex) {
        return String.format("%s%s%s%s", ERROR_FORMAT, ex.getMessage(), HELP_FORMAT, ex.getHelp());
    }

    protected abstract void printPrompt();

    protected abstract String eval(String input);
}
