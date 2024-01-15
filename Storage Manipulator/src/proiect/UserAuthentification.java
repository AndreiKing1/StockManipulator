package proiect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserAuthentification {

    private Connection connection; 

    public UserAuthentification() {

        initializeDatabaseConnection(); 
    }

    private void initializeDatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:your_database.db");
            createTableIfNotExists(); 
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            boolean userExists = resultSet.next();

            resultSet.close();
            statement.close();

            return userExists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public void registerUserInDatabase(String username, String password) {
        try {
            if (userExists(username)) {
                System.out.println("Utilizatorul există deja.");
                return;
            }
            
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean userExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        int count = 0;
        if (resultSet.next()) {
            count = resultSet.getInt(1);
        }
        resultSet.close();
        statement.close();
        return count > 0;
    }

    private void createTableIfNotExists() {
        try {
            String userQuery = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT)";
            PreparedStatement userStatement = connection.prepareStatement(userQuery);
            userStatement.executeUpdate();
            userStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
