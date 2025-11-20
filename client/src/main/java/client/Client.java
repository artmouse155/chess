package client;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public abstract class Client {

    protected static final String RESET_ALL = RESET_BG_COLOR + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT;
    private static final String ERROR_FORMAT = SET_TEXT_COLOR_RED;
    private static final String HELP_FORMAT = SET_TEXT_COLOR_BLUE;


    public abstract void run();

    protected String formatError(ClientException ex) {
        return String.format("%s%s%s%s", ERROR_FORMAT, ex.getMessage(), HELP_FORMAT, ex.getHelp());
    }

    protected abstract void printPrompt();

    protected abstract String eval(String input);
}
