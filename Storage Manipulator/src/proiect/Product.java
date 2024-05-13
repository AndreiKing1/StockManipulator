package proiect;

/**
 * Clasa ce reprezinta un produs intr-un sistem de gestionare a stocurilor.
 * 
 * @author Andrei
 * @version 26/01/2024
 */
public class Product {
    private int id; // ID-ul unic al produsului
    private String name; // Numele produsului
    private String category; // Categoria din care face parte produsul
    private double price; // Pretul produsului
    private int quantity; // Cantitatea disponibila in stoc

    /**
     * Constructor implicit pentru un produs.
     */
    public Product() {
    }

    /**
     * Constructor pentru initializarea unui produs cu detalii specifice.
     * 
     * @param id       ID-ul unic al produsului
     * @param name     Numele produsului
     * @param category Categoria din care face parte produsul
     * @param price    Pretul produsului
     * @param quantity Cantitatea disponibila in stoc
     */
    public Product(int id, String name, String category, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Returneaza ID-ul produsului.
     * 
     * @return ID-ul produsului
     */
    public int getId() {
        return id;
    }

    /**
     * Seteaza ID-ul produsului.
     * 
     * @param id ID-ul unic al produsului
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returneaza numele produsului.
     * 
     * @return Numele produsului
     */
    public String getName() {
        return name;
    }

    /**
     * Seteaza numele produsului.
     * 
     * @param name Numele produsului
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returneaza categoria produsului.
     * 
     * @return Categoria produsului
     */
    public String getCategory() {
        return category;
    }

    /**
     * Seteaza categoria produsului.
     * 
     * @param category Categoria din care face parte produsul
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returneaza pretul produsului.
     * 
     * @return Pretul produsului
     */
    public double getPrice() {
        return price;
    }

    /**
     * Seteaza pretul produsului.
     * 
     * @param price Pretul produsului
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returneaza cantitatea disponibila in stoc a produsului.
     * 
     * @return Cantitatea disponibila in stoc
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Seteaza cantitatea disponibila in stoc a produsului.
     * 
     * @param quantity Cantitatea disponibila in stoc
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returneaza o reprezentare sub forma de sir a numelui produsului.
     * 
     * @return Numele produsului sub forma de sir
     */
    public String toString() {
        return name;
    }
}
