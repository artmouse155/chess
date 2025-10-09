package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db",ctx -> ctx.result("{}"));
        server.post("user", this::register);
        server.post("session", ctx -> ctx.result("{\"username\": \"CHASE_IS COOL\", \"authToken\": 24601}"));
        server.delete("session", ctx -> ctx.result("{}"));
        server.get("game", ctx -> ctx.result("{}"));
        server.post("game", ctx -> ctx.result("{}"));
        server.put("game", ctx -> ctx.result("{}"));
    }

    private void register(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        var res = Map.of("username", req.get("username"), "authToken", "xyz");
        ctx.result(serializer.toJson(res));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
