package proiect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clasa care gestioneaza autentificarea utilizatorilor si operatiunile legate de acestia in cadrul bazei de date.
 * 
 * @author Andrei
 * @version 26/01/2024
 */
public class UserAuthentification {

    private Connection connection;

    /**
     * Constructorul clasei UserAuthentification.
     */
    public UserAuthentification() {
        initializeDatabaseConnection();
    }

    /**
     * Initializeaza conexiunea cu baza de date si creeaza tabela de utilizatori daca nu exista deja.
     */
    private void initializeDatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:your_database.db");
            createTableIfNotExists();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Autentifica un utilizator in functie de numele de utilizator si parola.
     * 
     * @param username Numele de utilizator
     * @param password Parola utilizatorului
     * @return Numele de utilizator autentificat sau null daca autentificarea a esuat
     */
    public String authenticateUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Utilizatorul exista, intoarce numele de utilizator
                String authenticatedUser = resultSet.getString("username");

                resultSet.close();
                statement.close();

                return authenticatedUser;
            } else {
                // Utilizatorul nu exista sau parola este incorecta
                resultSet.close();
                statement.close();

                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inregistreaza un nou utilizator in baza de date.
     * 
     * @param username Numele de utilizator
     * @param password Parola utilizatorului
     * @return 0 pentru succes, -1 daca utilizatorul exista deja, 1 pentru alta eroare
     */
    public int registerUserInDatabase(String username, String password) {
        try {
            if (userExists(username)) {
                return -1; // Indica ca utilizatorul exista deja
            }
            
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            statement.close();
            
            return 0; // Indica succesul inregistrarii
        } catch (SQLException e) {
            e.printStackTrace();
            return 1; // Indica o alta eroare
        }
    }

    /**
     * Verifica daca un utilizator exista in baza de date in functie de numele de utilizator.
     * 
     * @param username Numele de utilizator
     * @return true daca utilizatorul exista, false altfel
     * @throws SQLException Exceptie aruncata in caz de eroare la nivelul bazei de date
     */
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

    /**
     * Creeaza tabela de utilizatori daca nu exista deja.
     */
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
