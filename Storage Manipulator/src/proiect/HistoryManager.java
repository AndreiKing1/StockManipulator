package proiect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Managerul de istoric care gestioneaza inregistrarile de istoric ale actiunilor utilizatorilor.
 * 
 * @author Andrei
 * @version 26/01/2024
 */
public class HistoryManager {
    private List<HistoryEntry> historyEntries;
    private Connection connection;

    /**
     * Constructor care initializeaza managerul de istoric cu o lista goala de intrari de istoric.
     */
    public HistoryManager() {
        this.historyEntries = new ArrayList<>();
    }

    /**
     * Constructor care initializeaza managerul de istoric cu o lista goala de intrari de istoric si o conexiune la baza de date.
     * 
     * @param connection Conexiunea la baza de date
     */
    public HistoryManager(Connection connection) {
        this.historyEntries = new ArrayList<>();
        this.connection = connection;
    }

    /**
     * Obtine o conexiune la baza de date.
     * 
     * @return Conexiune la baza de date
     * @throws SQLException Exceptie aruncata in caz de eroare la nivelul bazei de date
     */
    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:your_database.db";
        return DriverManager.getConnection(url);
    }

    /**
     * Returneaza o lista de intrari de istoric din baza de date.
     * 
     * @return Lista de intrari de istoric
     */
    public List<HistoryEntry> getHistoryFromDatabase() {
        List<HistoryEntry> historyEntries = new ArrayList<>();

        String query = "SELECT * FROM history";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String timestamp = resultSet.getString("timestamp");
                String username = resultSet.getString("username");
                String action = resultSet.getString("action");

                HistoryEntry entry = new HistoryEntry(id, timestamp, username, action);
                historyEntries.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historyEntries;
    }

    /**
     * Adauga o intrare de istoric in baza de date si in lista locala de intrari de istoric.
     * 
     * @param historyEntry Intrarea de istoric de adaugat
     */
    public void addHistoryEntry(HistoryEntry historyEntry) {
        String insertQuery = "INSERT INTO history (timestamp, username, action) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, historyEntry.getTimestamp());
            statement.setString(2, historyEntry.getUsername());
            statement.setString(3, historyEntry.getAction());

            statement.executeUpdate();

            // Adauga istoricul in lista locala dupa ce a fost salvat in baza de date
            addEntry(historyEntry);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returneaza lista de intrari de istoric.
     * 
     * @return Lista de intrari de istoric
     */
    public List<HistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    /**
     * Returneaza o lista de siruri formatate care reprezinta toate intrarile de istoric.
     * 
     * @return Lista de siruri formatate pentru intrarile de istoric
     */
    public List<String> getAllHistoryEntries() {
        List<String> formattedEntries = new ArrayList<>();
        for (HistoryEntry entry : historyEntries) {
            formattedEntries.add(entry.toString());
        }
        return formattedEntries;
    }

    /**
     * Adauga o intrare de istoric in lista locala.
     * 
     * @param historyEntry Intrarea de istoric de adaugat
     */
    public void addEntry(HistoryEntry historyEntry) {
        historyEntries.add(historyEntry);
    }
}
