package proiect;

/**
 * Reprezentarea unei intrari de istoric cu informatii precum ID-ul, timestamp-ul, numele de utilizator si actiunea asociata.
 * 
 * @author Andrei
 * @version 26/01/2024
 */
class HistoryEntry {
    private int id;
    private String timestamp;
    private String username;
    private String action;

    /**
     * Constructor care initializeaza o intrare de istoric cu valorile date.
     * 
     * @param id         ID-ul intrarii de istoric
     * @param timestamp  Timestamp-ul intrarii de istoric
     * @param username   Numele de utilizator asociat intrarii de istoric
     * @param action     Actiunea asociata intrarii de istoric
     */
    public HistoryEntry(int id, String timestamp, String username, String action) {
        this.id = id;
        this.timestamp = timestamp;
        this.username = username;
        this.action = action;
    }

    /**
     * Returneaza ID-ul intrarii de istoric.
     * 
     * @return ID-ul intrarii de istoric
     */
    public int getId() {
        return id;
    }

    /**
     * Returneaza timestamp-ul intrarii de istoric.
     * 
     * @return Timestamp-ul intrarii de istoric
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Returneaza numele de utilizator asociat intrarii de istoric.
     * 
     * @return Numele de utilizator asociat intrarii de istoric
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returneaza actiunea asociata intrarii de istoric.
     * 
     * @return Actiunea asociata intrarii de istoric
     */
    public String getAction() {
        return action;
    }

    /**
     * Returneaza o reprezentare text a intrarii de istoric.
     * 
     * @return Sirul format care reprezinta intrarea de istoric
     */
    @Override
    public String toString() {
        return id + " - " + timestamp + " - " + username + " - " + action;
    }
}
