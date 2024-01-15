package proiect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        try {

            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:your_database.db");
            

            createTableIfNotExists();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:your_database.db");
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        
        try {
            Connection connection = getConnection(); // presupunând că ai o metodă getConnection() pentru a obține conexiunea la baza de date
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



    public void addProduct(Product product) throws SQLException {
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);  // Începe tranzacția

            // Verifică dacă produsul există deja în baza de date
            if (productExists(product.getName())) {
                String message = "Produsul exista deja in baza de date";
                JOptionPane.showMessageDialog(null, message, "Produs Există", JOptionPane.WARNING_MESSAGE);
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
