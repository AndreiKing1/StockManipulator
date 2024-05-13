package proiect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Clasa care gestioneaza operatiunile legate de produse in cadrul bazei de date.
 * 
 * @author Andrei
 * @version 26/01/2024
 */
public class ProductDAO {
    private Connection connection;
    /**
     * Constructorul clasei ProductDAO.
     */
    public ProductDAO() {
        try {

            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:your_database.db");
            

            createTableIfNotExists();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Metoda privata pentru crearea tabelelor daca nu exista deja.
     */
    private void createTableIfNotExists() {
        try {

            String productQuery = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "category TEXT," +
                    "price REAL," +
                    "quantity INTEGER)";
            PreparedStatement productStatement = connection.prepareStatement(productQuery);
            productStatement.executeUpdate();
            productStatement.close();


            String userQuery = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT)";
            PreparedStatement userStatement = connection.prepareStatement(userQuery);
            userStatement.executeUpdate();
            userStatement.close();
            String historyQuery = "CREATE TABLE IF NOT EXISTS history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "timestamp TEXT," +
                    "username TEXT," +
                    "action TEXT)";
            PreparedStatement historyStatement = connection.prepareStatement(historyQuery);
            historyStatement.executeUpdate();
            historyStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Returneaza ID-ul produsului pe baza numelui acestuia.
     * 
     * @param productName Numele produsului
     * @return ID-ul produsului sau -1 daca produsul nu este gasit
     */
    public int getProductIdByName(String productName) {
        try {
            String query = "SELECT id FROM products WHERE name = ?";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, productName);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // sau alta valoare de semnalare a lipsei de rezultate
    }
    /**
     * Adauga o intrare in tabela de istoric pentru o actiune efectuata de un utilizator.
     * 
     * @param username Numele utilizatorului care a efectuat actiunea
     * @param action   Actiunea efectuata
     */
    public void addHistoryEntry(String username, String action) {
        String query = "INSERT INTO history (timestamp, username, action) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, getCurrentTimestamp());
            statement.setString(2, username);
            statement.setString(3, action);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Returneaza marca temporala curenta sub forma de sir de caractere.
     * 
     * @return Sir de caractere reprezentand marca temporala curenta
     */
    public String getCurrentTimestamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentTime.format(formatter);
    }


    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:your_database.db");
    }
    /**
     * Returneaza o lista cu toate categoriile distincte existente in tabela de produse.
     * 
     * @return Lista de categorii
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        
        try {
            Connection connection = getConnection(); // presupunand ca ai o metoda getConnection() pentru a obtine conexiunea la baza de date
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT category FROM products");

            while (resultSet.next()) {
                categories.add(resultSet.getString("category"));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }
    /**
     * Returneaza o lista de produse dintr-o anumita categorie.
     * 
     * @param category Categorie pentru filtrarea produselor
     * @return Lista de produse din categoria specificata
     */
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();

        try {
            Connection connection = getConnection();
            String query = "SELECT * FROM products WHERE category = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, category);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setCategory(resultSet.getString("category"));
                product.setPrice(resultSet.getDouble("price"));
                product.setQuantity(resultSet.getInt("quantity"));
                products.add(product);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Returneaza o lista cu toate produsele din tabela de produse.
     * 
     * @return Lista de produse
     */
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();

        try {
            String query = "SELECT * FROM products";
            System.out.println("Executing query: " + query);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setCategory(resultSet.getString("category"));
                product.setPrice(resultSet.getDouble("price"));
                product.setQuantity(resultSet.getInt("quantity"));

                productList.add(product);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return productList;
    }


    /**
     * Adauga un produs nou in tabela de produse.
     * 
     * @param product Produsul de adaugat
     * @throws SQLException Exceptie aruncata in caz de eroare la nivelul bazei de date
     */
    public void addProduct(Product product) throws SQLException {
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);  // Incepe tranzactia

            // Verifica daca produsul exista deja in baza de date
            if (productExists(product.getName())) {
                String message = "Produsul exista deja in baza de date";
                JOptionPane.showMessageDialog(null, message, "Produs Exista", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "INSERT INTO products (name, category, price, quantity) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query);

            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQuantity());

            statement.executeUpdate();
            connection.commit();  
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                connection.rollback();  
            }
            throw e;
        } finally {
            if (statement != null) {
                statement.close();
            }
            connection.setAutoCommit(true);  
        }
    }

    /**
     * Verifica daca exista deja un produs cu un anumit nume in tabela de produse.
     * 
     * @param productName Numele produsului de verificat
     * @return true daca produsul exista, false altfel
     * @throws SQLException Exceptie aruncata in caz de eroare la nivelul bazei de date
     */
    private boolean productExists(String productName) throws SQLException {
        String query = "SELECT COUNT(*) FROM products WHERE name = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, productName);
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
     * Actualizeaza cantitatea si pretul unui produs in tabela de produse.
     * 
     * @param productId   ID-ul produsului de actualizat
     * @param newQuantity Noua cantitate a produsului
     * @param newPrice    Noul pret al produsului
     * @throws SQLException Exceptie aruncata in caz de eroare la nivelul bazei de date
     */
    public void updateProduct(int productId, int newQuantity, double newPrice) throws SQLException {
        try {
            String query = "UPDATE products SET quantity = ?, price = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, newQuantity);
            statement.setDouble(2, newPrice);
            statement.setInt(3, productId);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * Sterge un produs din tabela de produse.
     * 
     * @param product Produsul de sters
     */
    public void deleteProduct(Product product) {
        try {
            String query = "DELETE FROM products WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, product.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Cauta produse in functie de un termen de cautare in nume.
     * 
     * @param searchTerm Termenul de cautare in nume
     * @return Lista de produse care corespund termenului de cautare
     */
    public List<Product> searchProducts(String searchTerm) {
        List<Product> searchResults = new ArrayList<>();

        try {
            String query = "SELECT * FROM products WHERE name LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setCategory(resultSet.getString("category"));
                product.setPrice(resultSet.getDouble("price"));
                product.setQuantity(resultSet.getInt("quantity"));

                searchResults.add(product);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return searchResults;
    }
    /**
     * Returneaza un produs din tabela de produse in functie de ID-ul specificat.
     * 
     * @param productId ID-ul produsului cautat
     * @return Produsul corespunzator ID-ului sau null daca nu a fost gasit
     * @throws SQLException Exceptie aruncata in caz de eroare la nivelul bazei de date
     */
    public Product getProductById(int productId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT * FROM products WHERE id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, productId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                return new Product(productId, name, category, price, quantity);
            }
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return null; // Returnam null daca nu gasim produsul cu ID-ul respectiv
    }

    /**
     * Inchide resursele specifice bazei de date (conexiune, statement si resultSet).
     * 
     * @param connection Conexiunea la baza de date
     * @param statement  Statement-ul SQL
     * @param resultSet  Rezultatul interogarii SQL
     */
    private void closeResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Inchide conexiunea la baza de date, daca aceasta este deschisa.
     */
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
