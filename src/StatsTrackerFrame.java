import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Map;

public class StatsTrackerFrame extends JFrame {
    private PlayerManager playerManager;
    private Stopwatch stopwatch;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel stopwatchLabel;
    private String selectedPlayer;

    public StatsTrackerFrame() {
        playerManager = new PlayerManager();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Volleyball Stats Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        DefaultListModel<String> playerListModel = new DefaultListModel<>();
        JList<String> playerList = new JList<>(playerListModel);
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.setBackground(new Color(50, 50, 50));
        playerList.setForeground(Color.WHITE);

        JScrollPane playerListScrollPane = new JScrollPane(playerList);
        playerListScrollPane.setPreferredSize(new Dimension(150, getHeight()));
        add(playerListScrollPane, BorderLayout.WEST);

        DefaultListModel<String> timestampModel = new DefaultListModel<>();
        JList<String> timestampList = new JList<>(timestampModel);
        JScrollPane statsScrollPane = new JScrollPane(timestampList);
        statsScrollPane.setBackground(new Color(40, 40, 40));
        add(statsScrollPane, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(1, 0));
        statsPanel.setBackground(new Color(30, 30, 30));
        for (StatType stat : StatType.values()) {
            JButton button = createDarkStatButton(stat.toString(), e -> incrementStat(stat));
            statsPanel.add(button);
        }
        add(statsPanel, BorderLayout.NORTH);

        JPanel rightPanel = new JPanel(new GridLayout(0, 1));
        JButton addPlayerButton = createDarkButton("Add Player", e -> addPlayer(playerListModel));
        JButton saveButton = createDarkButton("Save Data", e -> DataSaver.saveStatsToFile(statsTable));
        JButton setTimerButton = createDarkButton("Set Timer", e -> setStopwatchTimer());
        JButton startStopwatchButton = createDarkButton("Start/Continue Stopwatch", e -> toggleStopwatch());
        JButton pauseStopwatchButton = createDarkButton("Pause Stopwatch", e -> pauseStopwatch());
        JButton resetStopwatchButton = createDarkButton("Reset Stopwatch", e -> resetStopwatch());

        stopwatchLabel = new JLabel("Stopwatch: ");
        stopwatchLabel.setForeground(Color.WHITE);
        stopwatchLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        rightPanel.setBackground(new Color(30, 30, 30));
        rightPanel.add(addPlayerButton);
        rightPanel.add(setTimerButton);
        rightPanel.add(startStopwatchButton);
        rightPanel.add(pauseStopwatchButton);
        rightPanel.add(resetStopwatchButton);
        rightPanel.add(saveButton);
        rightPanel.add(stopwatchLabel);
        add(rightPanel, BorderLayout.EAST);

        tableModel = new StatsTableModel();
        statsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(statsTable);
        tableScrollPane.setPreferredSize(new Dimension(400, getHeight()));
        add(tableScrollPane, BorderLayout.SOUTH);

        stopwatch = new Stopwatch(stopwatchLabel);
        setLocationRelativeTo(null);
        setVisible(true);

        playerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedPlayer = playerList.getSelectedValue();
                timestampList.setModel(playerManager.getTimestampListModel(selectedPlayer));
                tableModel.setDataVector(playerManager.getStatsTableData(), playerManager.getStatsTableHeaders());
            }
        });
    }

    private JButton createDarkButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.setBackground(new Color(255, 50, 50));
        button.setForeground(Color.WHITE);
        return button;
    }

    private JButton createDarkStatButton(String text, ActionListener listener) {
        JButton button = createDarkButton(text, listener);
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private void addPlayer(DefaultListModel<String> model) {
        String name = JOptionPane.showInputDialog(this, "Enter player name:");
        if (name != null && !name.trim().isEmpty()) {
            playerManager.addPlayer(name);
            model.addElement(name);
        }
    }

    private void incrementStat(StatType stat) {
        if (selectedPlayer != null) {
            String timestamp = Stopwatch.formatElapsedTime(stopwatch.elapsedTime());
            playerManager.incrementStat(selectedPlayer, stat.getLabel(), timestamp);
            tableModel.setDataVector(playerManager.getStatsTableData(), playerManager.getStatsTableHeaders());
        }
    }

    private void setStopwatchTimer() {
        String minutesInput = JOptionPane.showInputDialog(this, "Enter minutes:");
        String secondsInput = JOptionPane.showInputDialog(this, "Enter seconds:");
        try {
            int minutes = Integer.parseInt(minutesInput);
            int seconds = Integer.parseInt(secondsInput);
            stopwatch.setTimer((minutes * 60 + seconds) * 1000);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid time format.");
        }
    }

    private void toggleStopwatch() {
        stopwatch.toggle();
    }

    private void pauseStopwatch() {
        stopwatch.pause();
    }

    private void resetStopwatch() {
        stopwatch.reset();
    }
}