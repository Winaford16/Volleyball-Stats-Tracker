import javax.swing.*;
import java.util.*;

public class PlayerManager {
    private final Map<String, Map<String, Integer>> playerStatsMap = new HashMap<>();
    private final Map<String, DefaultListModel<String>> playerTimestampMap = new HashMap<>();

    private final List<String> allStatLabels;

    public PlayerManager() {
        allStatLabels = new ArrayList<>();
        for (StatType stat : StatType.values()) {
            allStatLabels.add(stat.getLabel());
        }
    }

    public void addPlayer(String name) {
        Map<String, Integer> stats = new HashMap<>();
        for (String stat : allStatLabels) {
            stats.put(stat, 0);
        }
        playerStatsMap.put(name, stats);
        playerTimestampMap.put(name, new DefaultListModel<>());
    }

    public void incrementStat(String playerName, String stat, String timestamp) {
        Map<String, Integer> stats = playerStatsMap.get(playerName);
        DefaultListModel<String> listModel = playerTimestampMap.get(playerName);
        if (stats == null || listModel == null) return;

        int newValue = stats.getOrDefault(stat, 0) + 1;
        stats.put(stat, newValue);

        String display = String.format("%s: %d (%s)", stat, newValue, timestamp);
        listModel.addElement(display);
    }

    public void deleteStat(String playerName, int index) {
        DefaultListModel<String> listModel = playerTimestampMap.get(playerName);
        if (listModel == null || index < 0 || index >= listModel.size()) return;

        String line = listModel.get(index);
        String statName = line.split(":")[0].trim();
        listModel.remove(index);

        Map<String, Integer> stats = playerStatsMap.get(playerName);
        if (stats != null && stats.containsKey(statName)) {
            stats.put(statName, stats.get(statName) - 1);
        }
    }

    public DefaultListModel<String> getTimestampListModel(String playerName) {
        return playerTimestampMap.getOrDefault(playerName, new DefaultListModel<>());
    }

    public Map<String, Map<String, Integer>> getAllStats() {
        return playerStatsMap;
    }

    // These two methods simulate the "getStatsTableData" and "getStatsTableHeaders"
    public Object[][] getStatsTableData() {
        Object[][] data = new Object[playerStatsMap.size()][allStatLabels.size() + 1];
        int i = 0;
        for (Map.Entry<String, Map<String, Integer>> entry : playerStatsMap.entrySet()) {
            data[i][0] = entry.getKey(); // player name
            for (int j = 0; j < allStatLabels.size(); j++) {
                data[i][j + 1] = entry.getValue().getOrDefault(allStatLabels.get(j), 0);
            }
            i++;
        }
        return data;
    }

    public Object[] getStatsTableHeaders() {
        Object[] headers = new Object[allStatLabels.size() + 1];
        headers[0] = "Player";
        for (int i = 0; i < allStatLabels.size(); i++) {
            headers[i + 1] = allStatLabels.get(i);
        }
        return headers;
    }

    // Optional helper if needed anywhere else
    public List<String> getAllStatLabels() {
        return allStatLabels;
    }
}
