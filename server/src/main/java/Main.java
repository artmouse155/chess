import chess.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.Server;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println(args.length);

        Server server;
        if (args.length >= 1 && args[0].equals("sql")) {
            server = new Server(true);
        } else {
            server = new Server();
        }

        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}