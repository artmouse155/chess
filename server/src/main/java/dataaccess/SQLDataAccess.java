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
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private <T extends Record> Set<T> getTableAsSet(String statement, Reader<T> reader) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
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

    private UserData readUserData(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameJSON = rs.getString("gameJSON");
        var game = new Gson().fromJson(gameJSON, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user_data (
              `username` varchar(128) NOT NULL,
              `password` varchar(128) NOT NULL,
              `email` varchar(128) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth_data (
              `username` varchar(128) NOT NULL,
              `password` varchar(128) NOT NULL,
              `email` varchar(128) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game_data (
              `username` varchar(128) NOT NULL,
              `password` varchar(128) NOT NULL,
              `email` varchar(128) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            )
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
                "UserDataSet",
                getTableAsSet("SELECT * FROM user_data", this::readUserData),
                "AuthDataSet",
                getTableAsSet("SELECT * FROM auth_data", this::readAuthData),
                "GameDataSet",
                getTableAsSet("SELECT * FROM game_data", this::readGameData)
        );
    }

    @Override
    public void deleteDB() throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public boolean hasUser(String username) throws DataAccessException {
        return false;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public boolean hasAuth(String authToken) throws DataAccessException {
        return false;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

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

    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {

    }

    @Override
    public void removeGame(int gameID) throws DataAccessException {

    }
}
