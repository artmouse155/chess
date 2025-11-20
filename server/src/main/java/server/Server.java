package server;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;

import handler.Handler;
import handler.WebSocketHandler;
import handler.exception.ResponseException;

import io.javalin.*;
import io.javalin.http.Context;

import io.javalin.websocket.WsMessageContext;
import model.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.function.Function;


public class Server {

    private final Javalin server;
    private Handler handler;
    private final WebSocketHandler wsHandler;
    private final String authKey = "authorization";

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
        server.ws("unauthGame", ws -> {
            ws.onConnect(ctx -> {
                ctx.enableAutomaticPings();
                System.out.println("Websocket connected");
            });
            ws.onMessage(this::webSocketMessage);
            ws.onClose(_ -> System.out.println("Websocket closed"));
        });

        server.exception(ResponseException.class, this::exceptionHandler);

        handler = new Handler(true);
        wsHandler = new WebSocketHandler();
    }

    public Server(boolean doSql) {
        this();
        handler = new Handler(doSql);
    }

    public void authenticate(Context ctx) throws ResponseException {
        var info = String.format("🔑 Auth: %s %s", ctx.method().name(), ctx.path());
        System.out.println(info);
        var authData = handler.handleAuth(ctx.header(authKey));
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
        var req = serializer.fromJson(ctx.body(), LoginRequest.class);
        var res = handler.handleLogin(req);
        ctx.result(res.toString());
    }

    public void logout(Context ctx) throws ResponseException {
        var res = handler.handleLogout(ctx.header(authKey));
        ctx.result(res.toString());
    }

    public void listGames(Context ctx) throws ResponseException {
        var res = handler.handleGetGames();
        ctx.result(res.toString());
    }

    public void createGame(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), CreateGameRequest.class);
        var res = handler.handleCreateGame(req.gameName());
        ctx.result(res.toString());
    }

    public void joinGame(Context ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.body(), JoinGameRequest.class);
        AuthData authData = ctx.attribute("authData");
        if (authData == null) {
            authData = AuthData.empty();
        }
        var res = handler.handleJoinGame(authData, req.playerColor(), req.gameID());
        ctx.result(res.toString());
    }

    public void webSocketMessage(WsMessageContext ctx) throws ResponseException {
        var serializer = new Gson();
        var req = serializer.fromJson(ctx.message(), UserGameCommand.class);
        String authtoken = req.getAuthToken();
        int gameID = req.getGameID();

        var authData = wsHandler.handleAuth(authtoken, gameID);
        String username = authData.username();
        String body = req.getCommandBody();

        ServerMessage serverMessage = switch (req.getCommandType()) {
            case CONNECT -> wsHandler.connect(username, gameID);
            case MAKE_MOVE -> wsHandler.makeMove(new ChessMove(new ChessPosition(0, 0), new ChessPosition(0, 0)));
            case LEAVE -> wsHandler.leave(username, gameID);
            case RESIGN -> wsHandler.resign(username, gameID);
            case ECHO -> wsHandler.echo(body);
        };
        ctx.send(serverMessage.toString());
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
