package websocket.commands;

public class EchoCommand extends UserGameCommand {

    private final String echo;

    public EchoCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, String echo) {
        super(commandType, authToken, gameID);
        this.echo = echo;
    }

    public String getEcho() {
        return echo;
    }

}