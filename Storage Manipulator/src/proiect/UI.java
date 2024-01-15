package proiect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UI {
    private JFrame frame;
    private HistoryManager historyManager;
    private ProductDAO productDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private UserAuthentification userAuth;
    private JFrame loginFrame;
    private JTextArea historyTextArea;

    public UI(ProductDAO productDAO) {
        this.productDAO = productDAO;
        this.userAuth = new UserAuthentification();
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

                if (userAuth.authenticateUser(username, password)) {
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

                userAuth.registerUserInDatabase(username, password);
                JOptionPane.showMessageDialog(null, "Utilizator înregistrat cu succes.");

                usernameField.setText("");
                passwordField.setText("");
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
        JButton searchButton = new JButton("Caută");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog();
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
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(searchField, BorderLayout.NORTH);
        frame.getContentPane().add(searchButton, BorderLayout.NORTH);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

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

        JScrollPane scrollPane = new JScrollPane(table);

        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().add(refreshButton);
        frame.getContentPane().add(scrollPane);

        JButton addButton = new JButton("Adaugă produs");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddProductDialog();
            }
        });

        frame.getContentPane().add(addButton);

        JButton updateButton = new JButton("Actualizare produs");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpdateProductDialog();
            }
        });

        frame.getContentPane().add(updateButton);

        JButton deleteButton = new JButton("Șterge produs");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteProductDialog();
            }
        });

        frame.getContentPane().add(deleteButton);

        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton historyButton = new JButton("Istoric");
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Va urma-- pentru istoric
            }
        });
        buttonPanel.add(historyButton);

        // Aici ar trebui să se încheie metoda cu:
        JButton selectCategoryButton = new JButton("Selectează categorie");
        selectCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCategoryDialog();
            }
        });

        frame.getContentPane().add(selectCategoryButton, BorderLayout.NORTH);
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
        historyFrame.setSize(400, 300);

        JTextArea historyTextArea = new JTextArea();
        historyTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(historyTextArea);

        historyFrame.getContentPane().add(scrollPane);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        historyFrame.setLocationRelativeTo(null);
        historyFrame.setVisible(true);

        displayHistory();
    }
    private void displayHistory() {
        List<String> historyEntries = historyManager.getHistoryEntries();

        historyTextArea.setText("");

        for (String entry : historyEntries) {
            historyTextArea.append(entry + "\n");
        }
    }

    private void addHistoryEntry(String entry) {

        historyManager.addEntry(entry);
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
                    Product selectedProduct = (Product) productComboBox.getSelectedItem();
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

    private void deleteProduct(Product product) {
        productDAO.deleteProduct(product);
    }

    private void updateProduct(int productId, int newQuantity, double newPrice) throws SQLException {
        productDAO.updateProduct(productId, newQuantity, newPrice);
    }


    private void addProduct(Product product) throws SQLException {
        productDAO.addProduct(product);
    }


    private void showSearchDialog() {
        JFrame searchFrame = new JFrame("Căutare produs");
        searchFrame.setSize(300, 150);

        JButton searchButton = new JButton("Caută");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                searchProducts(searchTerm);
                searchFrame.dispose();
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
        List<Product> filteredResults = new ArrayList<>();
        for (Product product : searchResults) {
            if (product.getName().contains(searchTerm)) {
                filteredResults.add(product);
            }
        }
        displaySearchResults(filteredResults);
    }

    private void displaySearchResults(List<Product> searchResults) {
        tableModel.setRowCount(0);

        for (Product product : searchResults) {
            Object[] rowData = {product.getId(), product.getName(), product.getCategory(), product.getPrice(), product.getQuantity()};
            tableModel.addRow(rowData);
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
                new UI(productDAO);
            }
        });
    }
}
