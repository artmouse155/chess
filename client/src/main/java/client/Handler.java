package client;

public abstract class Handler {


    protected static final String STRING = ".*";
    protected static final String STRING_128 = ".{0,128}";
    protected static final String POSITIVE_INTEGER = "\\d+";

    public interface TerminalFunction {

        String evaluate(String... params) throws ClientException;
    }

    protected void validateArgs(String[] args, String expectedMsg, String... regexes) throws ClientException {
        int size = regexes.length;

        if (args.length != size) {
            throw new ClientException(
                    String.format("Invalid number of arguments. (Expected %d, Found %d)\n", size, args.length),
                    expectedMsg
            );
        }
        for (int i = 0; i < size; i++) {
            if (!args[i].toUpperCase().matches(regexes[i])) {
                throw new ClientException(
                        String.format("Invalid argument \"%s\"\n", args[i]),
                        expectedMsg
                );
            }
        }
    }

    public abstract String help(String... params);

}
