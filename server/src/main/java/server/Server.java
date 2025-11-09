package server;

import com.google.gson.Gson;

import handler.exception.BadRequestException;
import handler.Handler;
import handler.exception.ResponseException;

import io.javalin.*;
import io.javalin.http.Context;

import model.AuthData;
import model.RegisterRequest;
import model.UserData;

import java.util.Map;


public class Server {

    private final Javalin server;
    private Handler handler;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);

        // Special authentication case. This authentication is called from within handler.
        server.delete("session", this::logout);

        server.before("game", this::authenticate);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

        server.exception(ResponseException.class, this::exceptionHandler);

        handler = new Handler(true);
    }

    public Server(boolean doSql) {
        this();
        handler = new Handler(doSql);
    }

    public void authenticate(Context ctx) throws ResponseException {
        var info = String.format("🔑 Auth: %s %s", ctx.method().name(), ctx.path());
        System.out.println(info);
        var authData = handler.handleAuth(ctx.header("authorization"));
        ctx.attribute("authData", authData);
    }


    public void clear(Context ctx) throws ResponseException {
        System.out.println("clear");
        ctx.result(handler.handleDeleteDB().toString());
    }

    public void clear() throws ResponseException {
        System.out.println("clear");
        handler.handleDeleteDB();
    }

    public void register(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), UserData.class);
        var res = handler.handleRegister(req);
        ctx.result(res.toString());
    }

    public void login(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), RegisterRequest.class);
        var res = handler.handleLogin(req);
        ctx.result(res.toString());
    }

    public void logout(Context ctx) throws ResponseException {
        var res = handler.handleLogout(ctx.header("authorization"));
        ctx.result(res.toString());
    }

    public void listGames(Context ctx) throws ResponseException {
        var res = handler.handleGetGames();
        ctx.result(res.toString());
    }

    public void createGame(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        var res = handler.handleCreateGame((String) req.get("gameName"));
        ctx.result(res.toString());
    }

    public void joinGame(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), Map.class);
        Double gameID = (Double) req.get("gameID");
        if (gameID == null) {
            throw new BadRequestException("That Game ID is not valid.");
        }
        var res = handler.handleJoinGame((AuthData) ctx.attribute("authData"), (String) req.get("playerColor"), gameID.intValue());
        ctx.result(res.toString());
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
