import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.Component;

public class PlayerManager {
    private final Map<String, Map<String, Integer>> playerStatsMap = new HashMap<>();
    private final Map<String, DefaultListModel<String>> playerTimestampMap = new HashMap<>();

    private final DefaultListModel<String> playerListModel = new DefaultListModel<>();
    private final Map<String, DefaultListModel<String>> playerListModelMap = new HashMap<>();

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

        DefaultListModel<String> timestampModel = new DefaultListModel<>();
        playerTimestampMap.put(name, timestampModel);
        playerListModel.addElement(name);
        playerListModelMap.put(name, timestampModel);
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

    public List<String> getTimestamps(String player) {
        DefaultListModel<String> model = playerListModelMap.get(player);
        List<String> list = new ArrayList<>();
        if (model != null) {
            for (int i = 0; i < model.size(); i++) {
                list.add(model.getElementAt(i));
            }
        }
        return list;
    }

    public DefaultListModel<String> getPlayerListModel() {
        return playerListModel;
    }

    public Object[][] getStatsTableData() {
        Object[][] data = new Object[playerStatsMap.size()][allStatLabels.size() + 1];
        int i = 0;
        for (Map.Entry<String, Map<String, Integer>> entry : playerStatsMap.entrySet()) {
            data[i][0] = entry.getKey();
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

    public void saveData(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, Map<String, Integer>> entry : playerStatsMap.entrySet()) {
                    writer.write(entry.getKey());
                    writer.newLine();
                    for (Map.Entry<String, Integer> stat : entry.getValue().entrySet()) {
                        writer.write(stat.getKey() + ": " + stat.getValue());
                        writer.newLine();
                    }
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(parent, "Data saved successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error saving data: " + e.getMessage());
            }
        }
    }

    public List<String> getAllStatLabels() {
        return allStatLabels;
    }
}
