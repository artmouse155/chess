package client;

import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public abstract class Client {

    protected static final String RESET_ALL = RESET_BG_COLOR + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT;
    private static final String ERROR_FORMAT = SET_TEXT_COLOR_RED;
    private static final String HELP_FORMAT = SET_TEXT_COLOR_BLUE;
    protected static final String APP_TITLE_FORMAT = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK;
    protected static final String USERNAME_FORMAT = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE;


    public String run() {
        System.out.print(getIntroMessage());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            result = eval(line);
            if (getTerminalStrings().contains(result)) {
                return result;
            }

            System.out.print(result);
        }
    }

    protected abstract Collection<String> getTerminalStrings();

    protected abstract String getIntroMessage();

    protected String formatError(ClientException ex) {
        return String.format("%s%s%s%s", ERROR_FORMAT, ex.getMessage(), HELP_FORMAT, ex.getHelp());
    }

    protected abstract void printPrompt();

    protected abstract String eval(String input);
}
