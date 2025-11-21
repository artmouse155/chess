package client.repl;

import client.Handler;

import java.util.Collection;
import java.util.List;

public class ResignREPL extends REPL {
    @Override
    protected Collection<String> getTerminalStrings() {
        return List.of("y", "n");
    }

    @Override
    protected String getIntroMessage() {
        return "Resigning a game means that you don't get to play it anymore, which is sad. Are you sure you want to quit?\n";
    }

    @Override
    protected String getPrompt() {
        return "\"y\" to resign, \"n\" to return to game.\n> ";
    }

    @Override
    protected Handler.TerminalFunction getTerminalCommand(String cmd) {
        return switch (cmd) {
            case "y" -> (String... p) -> "y";
            case "n" -> (String... p) -> "n";
            default -> (String... p) -> "";
        };
    }
}
