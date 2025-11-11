package client;

public abstract class Client {

    public abstract void run();

    protected abstract void printPrompt();

    protected abstract String formatError(ClientException ex);

    public abstract String eval(String input);
}
