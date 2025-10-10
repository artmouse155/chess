package server;

import com.google.gson.Gson;
import handler.Handler;
import handler.ResponseException;
import handler.UnauthorizedException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

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

        server.before("game", this::authenticate);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

        server.exception(ResponseException.class, this::exceptionHandler);

        handler = new Handler();
    }

    private void authenticate(Context ctx) throws ResponseException {
        var info = String.format("🔑 Auth: %s %s", ctx.method().name(), ctx.path());
        System.out.println(info);
        var authData = handler.handleAuth(ctx.header("authorization"));
        ctx.attribute("authData", authData);
    }

    ;

    private void clear(Context ctx) throws ResponseException {
        var serializer = new Gson();
        System.out.println("clear");
        ctx.result(serializer.toJson(handler.handleDeleteDB()));
    }

    private void register(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), UserData.class);
        var res = handler.handleRegister(req);
        ctx.result(res.toString());
    }

    private void login(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        var res = handler.handleLogin((String) req.get("username"), (String) req.get("password"));
        ctx.result(res.toString());
    }

    private void logout(Context ctx) throws ResponseException {
        authenticate(ctx);
        var serializer = new Gson();
        var res = handler.handleLogout(ctx.header("authorization"));
        ctx.result(serializer.toJson(res));
    }

    private void listGames(Context ctx) throws ResponseException {
        // Do something...
        ctx.result("{\"games\" : []}");
    }

    private void createGame(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        // Do something...
        ctx.result("{\"gameID\": 24601}");
    }

    private void joinGame(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        // Do something...
        ctx.result("{}");
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.getHTTPCode());
        ctx.result(ex.toJson());
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
