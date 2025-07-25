import javax.swing.*;

public class Stopwatch {
    private long startTime;
    private long elapsed;
    private long timerDuration;
    private JLabel label;
    private Timer swingTimer;

    private boolean running = false;

    public Stopwatch(JLabel label) {
        this.label = label;
    }

    public void setTimer(long ms) {
        this.timerDuration = ms;
        updateLabel();
    }

    public void start() {
        if (!running) {
            startTime = System.currentTimeMillis();
            swingTimer = new Timer(1000, e -> updateLabel());
            swingTimer.start();
            running = true;
        }
    }

    public void stop() {
        if (running) {
            swingTimer.stop();
            elapsed += System.currentTimeMillis() - startTime;
            running = false;
            updateLabel();
        }
    }

    public void reset() {
        stop();
        elapsed = 0;
        timerDuration = 0;
        updateLabel();
    }

    public long elapsedTime() {
        if (running) {
            return elapsed + (System.currentTimeMillis() - startTime);
        }
        return elapsed;
    }

    public boolean isRunning() {
        return running;
    }

    public void pause() {
        stop();
    }

    public void toggle() {
        if (running) stop();
        else start();
    }

    public static String formatElapsedTime(long elapsedMillis) {
        long seconds = elapsedMillis / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private void updateLabel() {
        label.setText("Stopwatch: " + formatElapsedTime(elapsedTime()));
    }
}
