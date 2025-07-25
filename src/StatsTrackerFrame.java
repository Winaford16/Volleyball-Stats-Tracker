import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class StatsTrackerFrame extends JFrame {

    private PlayerManager playerManager;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JList<String> timestampList;
    private DefaultListModel<String> timestampModel;
    private String selectedPlayer;
    private Stopwatch stopwatch;

    public StatsTrackerFrame() {
        super("Volleyball Stats Tracker");
        playerManager = new PlayerManager();
        JLabel stopwatchLabel = new JLabel("Stopwatch: 00:00");
        stopwatch = new Stopwatch(stopwatchLabel);

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Left panel: player list
        JList<String> playerList = new JList<>(playerManager.getPlayerListModel());
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedPlayer = playerList.getSelectedValue();
                timestampModel.clear();
                timestampModel.addAll(playerManager.getTimestamps(selectedPlayer));
                tableModel.setDataVector(playerManager.getStatsTableData(), playerManager.getStatsTableHeaders());
            }
        });
        JScrollPane playerScroll = new JScrollPane(playerList);
        add(playerScroll, BorderLayout.WEST);

        // Center panel: stat buttons
        JPanel statPanel = new JPanel(new GridLayout(0, 2));
        for (StatType stat : StatType.values()) {
            JButton btn = new JButton(stat.getLabel());
            btn.addActionListener(e -> incrementStat(stat));
            statPanel.add(btn);
        }
        add(statPanel, BorderLayout.CENTER);

        // Right panel: controls
        JPanel rightPanel = new JPanel(new GridLayout(0, 1));
        JButton addPlayerBtn = new JButton("Add Player");
        addPlayerBtn.addActionListener(e -> addPlayer());
        JButton setTimerButton = new JButton("Set Timer");
        setTimerButton.addActionListener(e -> setStopwatchTimer());

        JButton startStopwatchButton = new JButton("Start/Continue Stopwatch");
        startStopwatchButton.addActionListener(e -> stopwatch.start());

        JButton pauseStopwatchButton = new JButton("Pause Stopwatch");
        pauseStopwatchButton.addActionListener(e -> stopwatch.stop());

        JButton resetStopwatchButton = new JButton("Reset Stopwatch");
        resetStopwatchButton.addActionListener(e -> stopwatch.reset());

        JButton loadVideoBtn = new JButton("Load Video");
        loadVideoBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File videoFile = fc.getSelectedFile();
                try {
                    VideoApiClient.loadVideo(videoFile.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to load video.");
                }
            }
        });

        JButton playVideoBtn = new JButton("Play Video");
        playVideoBtn.addActionListener(e -> {
            try {
                VideoApiClient.playVideo();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to play video.");
            }
        });

        JButton saveDataButton = new JButton("Save Data");
        saveDataButton.addActionListener(e -> playerManager.saveData(this));

        rightPanel.add(addPlayerBtn);
        rightPanel.add(setTimerButton);
        rightPanel.add(startStopwatchButton);
        rightPanel.add(pauseStopwatchButton);
        rightPanel.add(resetStopwatchButton);
        rightPanel.add(loadVideoBtn);
        rightPanel.add(playVideoBtn);
        rightPanel.add(saveDataButton);

        add(rightPanel, BorderLayout.EAST);

        // Bottom panel: stat table
        tableModel = new DefaultTableModel();
        statsTable = new JTable(tableModel);
        add(new JScrollPane(statsTable), BorderLayout.SOUTH);

        // Top panel: timestamps
        timestampModel = new DefaultListModel<>();
        timestampList = new JList<>(timestampModel);
        add(new JScrollPane(timestampList), BorderLayout.NORTH);

        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void incrementStat(StatType stat) {
        if (selectedPlayer != null) {
            try {
                int ms = VideoApiClient.getTimestampMs();
                String timestamp = Stopwatch.formatElapsedTime(ms);
                playerManager.incrementStat(selectedPlayer, stat.getLabel(), timestamp);
                tableModel.setDataVector(playerManager.getStatsTableData(), playerManager.getStatsTableHeaders());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to get video timestamp.");
            }
        }
    }

    private void addPlayer() {
        String name = JOptionPane.showInputDialog(this, "Enter player name:");
        if (name != null && !name.trim().isEmpty()) {
            playerManager.addPlayer(name.trim());
        }
    }

    private void setStopwatchTimer() {
        String minutes = JOptionPane.showInputDialog(this, "Enter minutes:");
        String seconds = JOptionPane.showInputDialog(this, "Enter seconds:");
        try {
            long duration = (Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds)) * 1000L;
            stopwatch.setTimer(duration);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid time entered.");
        }
    }
}