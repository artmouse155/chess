package websocket.commands;

public class EchoCommand extends UserGameCommand {

    private final String echo;

    public EchoCommand(String authToken, Integer gameID, String echo) {
        super(CommandType.ECHO, authToken, gameID);
        this.echo = echo;
    }

    public String getEcho() {
        return echo;
    }

}