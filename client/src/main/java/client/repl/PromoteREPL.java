package client.repl;

import client.Handler;

import java.util.Collection;
import java.util.List;

public class PromoteREPL extends REPL {
    @Override
    protected Collection<String> getTerminalStrings() {
        return List.of("q", "b", "k", "r", "c");
    }

    @Override
    protected String getIntroMessage() {
        return "If you do that move, you'll promote your pawn!\nSo... What'll it be?\n";
    }

    @Override
    protected String getPrompt() {
        return """
                "q" for queen
                "b" for bishop
                "k" for knight
                "r" for rook
                "c" for cancel
                > """;
    }

    @Override
    protected Handler.TerminalFunction getTerminalCommand(String cmd) {
        return switch (cmd) {
            case "q" -> (String... p) -> "q";
            case "b" -> (String... p) -> "b";
            case "k" -> (String... p) -> "k";
            case "r" -> (String... p) -> "r";
            case "c" -> (String... p) -> "c";
            default -> (String... p) -> "";
        };
    }
}
