package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.sql.Types.NULL;

public class SQLDataAccess implements DataAccess {

    private interface Reader<T> {
        T readFunc(ResultSet rs) throws SQLException;
    }

    public SQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                prepareStatement(ps, params);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private <T extends Record> Set<T> getTableAsSet(String statement, Reader<T> reader, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                prepareStatement(ps, params);
                try (var rs = ps.executeQuery()) {
                    var set = new HashSet<T>();
                    while (rs.next()) {
                        set.add(reader.readFunc(rs));
                    }
                    return set;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to get table from database with statement: \"%s\", %s", statement, e.getMessage()));
        }
    }

    private void prepareStatement(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            switch (param) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case null -> ps.setNull(i + 1, NULL);
                default -> {
                }
            }
        }
    }

    private UserData readUserData(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        var authToken = rs.getString("auth_token");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("game_id");
        var whiteUsername = rs.getString("white_username");
        var blackUsername = rs.getString("black_username");
        var gameName = rs.getString("game_name");
        var gameJSON = rs.getString("game_json");
//        System.out.println(gameJSON);
        var game = ChessGame.fromString(gameJSON);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user_data (
              `username` varchar(128) NOT NULL,
              `password` varchar(128) NOT NULL,
              `email` varchar(128) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth_data (
              `auth_token` varchar(128) NOT NULL,
              `username` varchar(128) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game_data (
              `game_id` int NOT NULL,
              `white_username` varchar(128),
              `black_username` varchar(128),
              `game_name` varchar(128),
              `game_json` LONGTEXT,
              PRIMARY KEY (`game_id`)
            );
            """,
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public Map<String, Set<? extends Record>> getDB() throws DataAccessException {
        return Map.of(
                "userDataSet",
                getTableAsSet("SELECT * FROM user_data", this::readUserData),
                "authDataSet",
                getTableAsSet("SELECT * FROM auth_data", this::readAuthData),
                "gameDataSet",
                getTableAsSet("SELECT * FROM game_data", this::readGameData)
        );
    }

    @Override
    public void deleteDB() throws DataAccessException {
        executeUpdate("TRUNCATE user_data");
        executeUpdate("TRUNCATE auth_data");
        executeUpdate("TRUNCATE game_data");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var outputSet = getTableAsSet("SELECT * FROM user_data WHERE username=?", this::readUserData, username);
        var optionalFirst = outputSet.stream().findFirst();
        return optionalFirst.orElseThrow(DataAccessException::new);
    }

    @Override
    public boolean hasUser(String username) throws DataAccessException {
        try {
            getUser(username);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        executeUpdate("INSERT INTO user_data (username, password, email) VALUES(?, ?, ?)", userData.username(), userData.password(), userData.email());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var outputSet = getTableAsSet("SELECT * FROM auth_data WHERE auth_token=?", this::readAuthData, authToken);
        var optionalFirst = outputSet.stream().findFirst();
        return optionalFirst.orElseThrow(DataAccessException::new);
    }

    @Override
    public boolean hasAuth(String authToken) throws DataAccessException {
        try {
            getAuth(authToken);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        executeUpdate("INSERT INTO auth_data (auth_token, username) VALUES(?, ?)", authData.authToken(), authData.username());
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {

    }

    @Override
    public Set<GameData> getGameDataSet() throws DataAccessException {
        return Set.of();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public boolean hasGame(int gameID) throws DataAccessException {
        return false;
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        executeUpdate(
                "INSERT INTO game_data (game_id, white_username, black_username, game_name, game_json) VALUES(?, ?, ?, ?, ?)",
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game().toString()
        );
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {

    }

    @Override
    public void removeGame(int gameID) throws DataAccessException {

    }
}
