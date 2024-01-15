package proiect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private List<String> historyEntries;

    public HistoryManager() {
        this.historyEntries = new ArrayList<>();
    }

    public void addEntry(String entry) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        String historyEntry = formattedTime + " - " + entry;
        historyEntries.add(historyEntry);
    }

    public List<String> getHistoryEntries() {
        return new ArrayList<>(historyEntries);
    }
}
