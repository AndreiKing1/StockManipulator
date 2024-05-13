package proiect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 * Clasa care gestioneaza interfata grafica a aplicatiei de gestionare a stocurilor.
 * Utilizeaza obiecte de tip {@link ProductDAO}, {@link HistoryManager} si {@link UserAuthentification}.
 * 
 * @author Andrei
 * @version 26/01/2024
 */
public class UI {
    private JFrame frame;
    private HistoryManager historyManager;
    private ProductDAO productDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private UserAuthentification userAuth;
    private JFrame loginFrame;
    private JTextArea historyTextArea;
    private String currentUser;
    private Product selectedProduct;

    public UI(ProductDAO productDAO, HistoryManager historyManager) {
        this.productDAO = productDAO;
        this.userAuth = new UserAuthentification();
        this.historyManager = historyManager;
        initializeLoginUI();
    }


    private JTextField searchField;

    private void initializeLoginUI() {
    	loginFrame = new JFrame("Autentificare");
        loginFrame.setSize(300, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Autentificare");
        JButton registerButton = new JButton("Înregistrare");

        loginButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registerButton.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 240, 240));

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Parolă:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                String authenticatedUser = userAuth.authenticateUser(username, password);
                if (authenticatedUser != null) {
                    currentUser = authenticatedUser;
                    loginFrame.dispose();
                    initializeMainUI();
                } else {
                    JOptionPane.showMessageDialog(null, "Autentificare eșuată. Verificați username-ul și parola.");
                }
            }
        });
        

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                int registrationResult = userAuth.registerUserInDatabase(username, password);
                
                if (registrationResult == 0) {
                    JOptionPane.showMessageDialog(null, "Utilizator înregistrat cu succes.");
                    usernameField.setText("");
                    passwordField.setText("");
                } else if (registrationResult == -1) {
                    JOptionPane.showMessageDialog(null, "Utilizatorul există deja.", "Avertisment", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Eroare la înregistrare.", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loginFrame.getContentPane().setLayout(new BorderLayout());
        loginFrame.getContentPane().add(panel, BorderLayout.CENTER);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private void initializeMainUI() {
        frame = new JFrame("Aplicație Gestionare Stocuri");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton searchButton = new JButton("Caută");
        JButton refreshButton = new JButton("Refresh");
        JButton addButton = new JButton("Adaugă produs");
        JButton updateButton = new JButton("Actualizare produs");
        JButton deleteButton = new JButton("Șterge produs");
        JButton historyButton = new JButton("Istoric");
        JButton selectCategoryButton = new JButton("Selectează categorie");
        

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddProductDialog();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpdateProductDialog();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteProductDialog();
            }
        });

        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHistoryDialog();
            }
        });

        selectCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCategoryDialog();
            }
        });

        searchField = new JTextField();
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                searchProducts(searchTerm);
            }
        });

        table = new JTable();
        tableModel = new DefaultTableModel();
        table.setModel(tableModel);

        tableModel.addColumn("ID");
        tableModel.addColumn("Nume");
        tableModel.addColumn("Categorie");
        tableModel.addColumn("Preț");
        tableModel.addColumn("Cantitate");


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);

        frame.getContentPane().setLayout(new BorderLayout());

        // Top panel for search and category selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(selectCategoryButton);
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);

        // Center panel for the table
        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        bottomPanel.add(addButton);
        bottomPanel.add(updateButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(historyButton);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        refreshTable();
    }

    private void showCategoryDialog() {
        List<String> categories = productDAO.getAllCategories();
        String[] categoryArray = categories.toArray(new String[0]);

        String selectedCategory = (String) JOptionPane.showInputDialog(
                frame,
                "Selectați o categorie:",
                "Selectare categorie",
                JOptionPane.QUESTION_MESSAGE,
                null,
                categoryArray,
                categoryArray[0]
        );

        if (selectedCategory != null) {
            showProductsByCategory(selectedCategory);
        }
    }

    private void showProductsByCategory(String category) {
        List<Product> productsInCategory = productDAO.getProductsByCategory(category);
        displaySearchResults(productsInCategory);
    }

    private void showHistoryDialog() {
        JFrame historyFrame = new JFrame("Istoric modificări");
        historyFrame.setSize(500, 400);

        historyTextArea = new JTextArea();
        historyTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(historyTextArea);

        historyFrame.getContentPane().add(scrollPane);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        historyFrame.setLocationRelativeTo(null);
        historyFrame.setVisible(true);

        displayHistory();
    }

    private void displayHistory() {
        List<HistoryEntry> historyEntries = historyManager.getHistoryFromDatabase();

        if (historyTextArea != null) {
            historyTextArea.setText("");

            for (HistoryEntry entry : historyEntries) {
                historyTextArea.append(entry.getTimestamp() + " - " + entry.getUsername() + " " + entry.getAction() + "\n");
            }
            historyTextArea.repaint();
        }
    }


    private void showAddProductDialog() {
        JFrame addProductFrame = new JFrame("Adăugare produs");
        addProductFrame.setSize(300, 200);

        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();
        JButton addButton = new JButton("Adăugare");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String category = categoryField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());

                    Product product = new Product(0, name, category, price, quantity);

                    addProduct(product);

                    addProductFrame.dispose();

                    refreshTable();
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Preț și Cantitate trebuie să fie numere valide.");
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Nume:"));
        panel.add(nameField);
        panel.add(new JLabel("Categorie:"));
        panel.add(categoryField);
        panel.add(new JLabel("Preț:"));
        panel.add(priceField);
        panel.add(new JLabel("Cantitate:"));
        panel.add(quantityField);
        panel.add(addButton);

        addProductFrame.getContentPane().add(panel);
        addProductFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addProductFrame.setLocationRelativeTo(null);

        addProductFrame.setVisible(true);
    }

    private void showUpdateProductDialog() {
        JFrame updateProductFrame = new JFrame("Actualizare produs");
        updateProductFrame.setSize(150, 150);

        List<Product> productList = productDAO.getAllProducts();
        JComboBox<Product> productComboBox = new JComboBox<>(productList.toArray(new Product[0]));
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JButton updateButton = new JButton("Actualizare");

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    selectedProduct = (Product) productComboBox.getSelectedItem();
                    int newQuantity = Integer.parseInt(quantityField.getText());
                    double newPrice = Double.parseDouble(priceField.getText());

                    updateProduct(selectedProduct.getId(), newQuantity, newPrice);

                    updateProductFrame.dispose();

                    refreshTable();
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Cantitate și Preț trebuie să fie valori numerice.");
                }
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Selectați produsul:"));
        panel.add(productComboBox);
        panel.add(new JLabel("Cantitate nouă:"));
        panel.add(quantityField);
        panel.add(new JLabel("Preț nou:"));
        panel.add(priceField);
        panel.add(updateButton);

        updateProductFrame.getContentPane().add(panel);
        updateProductFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        updateProductFrame.setLocationRelativeTo(null);
        updateProductFrame.setVisible(true);
    }

    private void showDeleteProductDialog() {
        JComboBox<Product> productComboBox = new JComboBox<>(productDAO.getAllProducts().toArray(new Product[0]));

        int result = JOptionPane.showConfirmDialog(null, productComboBox, "Selectați produsul de șters",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            showConfirmDeleteDialog(selectedProduct);
        }
    }

    private void showConfirmDeleteDialog(Product product) {
        int result = JOptionPane.showConfirmDialog(null,
                "Sigur doriți să ștergeți produsul \"" + product.getName() + "\"?",
                "Confirmare ștergere",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            deleteProduct(product);
            refreshTable();
        }
    }

    private void addProduct(Product product) throws SQLException {
        productDAO.addProduct(product);

        // Obține ID-ul produsului adăugat
        int productId = productDAO.getProductIdByName(product.getName());

        String action = "a adăugat produsul: " + product.getName();
        HistoryEntry historyEntry = new HistoryEntry(productId, getCurrentTimestamp(), currentUser, action);
        addHistoryEntry(historyEntry);
    }




    private void updateProduct(int productId, int newQuantity, double newPrice) throws SQLException {
        Product existingProduct = productDAO.getProductById(productId);

        if (existingProduct != null) {
            // Salvăm informații despre cantitatea și prețul vechi
            int oldQuantity = existingProduct.getQuantity();
            double oldPrice = existingProduct.getPrice();

            // Actualizăm produsul în baza de date
            productDAO.updateProduct(productId, newQuantity, newPrice);

            // Construim mesajul de istoric
            String action = "a actualizat produsul cu ID-ul " + productId +
                    " de la cantitatea " + oldQuantity + " și prețul " + oldPrice +
                    " la cantitatea " + newQuantity + " și prețul " + newPrice;

            // Adăugăm înregistrarea în istoric
            HistoryEntry historyEntry = new HistoryEntry(productId, getCurrentTimestamp(), currentUser, action);
            addHistoryEntry(historyEntry);
        }
    }


    private void deleteProduct(Product product) {
    	int productId = product.getId();
        productDAO.deleteProduct(product);

        String action = "a șters produsul: " + product.getName();
        HistoryEntry historyEntry = new HistoryEntry(productId, getCurrentTimestamp(), currentUser, action);
        addHistoryEntry(historyEntry);
    }

    private static String getCurrentTimestamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentTime.format(formatter);
    }

    private void showSearchDialog() {
        JFrame searchFrame = new JFrame("Căutare produs");
        searchFrame.setSize(200, 120);

        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Caută");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                searchProducts(searchTerm);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Introduceți termenul de căutare:"));
        panel.add(searchField);
        panel.add(searchButton);

        searchFrame.getContentPane().add(panel);
        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        searchFrame.setLocationRelativeTo(null);
        searchFrame.setVisible(true);
    }



    private void refreshTable() {
        tableModel.setRowCount(0);

        List<Product> productList = productDAO.getAllProducts();

        for (Product product : productList) {
            Object[] rowData = {product.getId(), product.getName(), product.getCategory(), product.getPrice(), product.getQuantity()};
            tableModel.addRow(rowData);
        }
    }

    private void searchProducts(String searchTerm) {
        List<Product> searchResults = productDAO.searchProducts(searchTerm);
        displaySearchResults(searchResults);

        String action = "a efectuat o căutare pentru: " + searchTerm;
        HistoryEntry searchHistoryEntry = new HistoryEntry(-1, getCurrentTimestamp(), currentUser, action);
        addHistoryEntry(searchHistoryEntry);
    }

    private void displaySearchResults(List<Product> searchResults) {
        tableModel.setRowCount(0);

        for (Product product : searchResults) {
            Object[] rowData = {product.getId(), product.getName(), product.getCategory(), product.getPrice(), product.getQuantity()};
            tableModel.addRow(rowData);
        }
    }

    public void addHistoryEntry(HistoryEntry historyEntry) {
        try {
            // Adaugă istoricul în managerul de istoric
            historyManager.addEntry(historyEntry);

            // Adaugă istoricul în baza de date
            productDAO.addHistoryEntry(historyEntry.getUsername(), historyEntry.getAction());

            // Actualizează afișarea istoricului în UI
            displayHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ProductDAO productDAO = new ProductDAO();
                HistoryManager historyManager = new HistoryManager();
                
                new UI(productDAO, historyManager);
            }
        });
    }
}
