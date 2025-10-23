package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;

import java.util.Map;
import java.util.Set;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDataAccess implements DataAccess {

    Connection getConnection() throws DataAccessException {
        final String user = "";
        final String password = "";
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306", user, password);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param instanceof PetType p) {
                        ps.setString(i + 1, p.toString());
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  pet (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public Map<String, Set<? extends Record>> getDB() throws DataAccessException {
        return Map.of();
    }

    @Override
    public void deleteDB() throws DataAccessException {
        try (var conn = getConnection()) {
            // Do something...
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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
