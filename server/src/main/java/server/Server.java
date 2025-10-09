package server;

import com.google.gson.Gson;
import handler.Handler;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final Handler handler;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);

        // Special authentication case.
        server.delete("session", this::logout);
        
        server.before("game",this::authenticate);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

        handler = new Handler();
    }

    private void authenticate(Context ctx) {
        var info = String.format("🔑 Auth: %s %s", ctx.method().name(), ctx.path());
        System.out.println(info);
    }

    ;
    private void clear(Context ctx) {
        // Do Something...
        handler.deleteDB();
        System.out.println("clear");
        ctx.result("{}");
    }

    private void register(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
//        var res = Map.of("username", req.get("username"), "authToken", "xyz");
        var res = handler.handleRegister((String) req.get("username"), (String) req.get("password"), (String) req.get("email"));
        ctx.result(serializer.toJson(res));
    }

    private void login(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
//        var res = Map.of("username", req.get("username"), "authToken", "xyz");
        var res = handler.handleLogin((String) req.get("username"), (String) req.get("password"));
        ctx.result(serializer.toJson(res));
    }

    private void logout(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        // Do something...
        var res = handler.handleLogout((String) req.get("authToken"));
        ctx.result();
    }

    private void listGames(Context ctx) {
        // Do something...
        ctx.result("{\"games\" : []}");
    }

    private void createGame(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        // Do something...
        ctx.result("{\"gameID\": 24601}");
    }

    private void joinGame(Context ctx) {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        // Do something...
        ctx.result("{}");
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
