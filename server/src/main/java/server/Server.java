package server;

import io.javalin.*;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db",ctx -> ctx.result("{}"));
        server.post("user", ctx-> ctx.result("{\"username\": \"CHASE_IS COOL\", \"authToken\": 24601}"));
        server.post("session", ctx -> ctx.result("{\"username\": \"CHASE_IS COOL\", \"authToken\": 24601}"));
        server.delete("session", ctx -> ctx.result("{}"));
        server.get("game", ctx -> ctx.result("{}"));
        server.post("game", ctx -> ctx.result("{}"));
        server.put("game", ctx -> ctx.result("{}"));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
