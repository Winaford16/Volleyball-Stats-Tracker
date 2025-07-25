public enum StatType {
    KILLS("Kills"),
    DIGS("Digs"),
    BLOCKS("Blocks"),
    ACES("Aces"),
    MISSED_SERVES("Missed Serves"),
    ERRORS("Errors"),
    ATTACKS("Attacks"),
    ONE_PASS("1 Pass"),
    TWO_PASS("2 Pass"),
    THREE_PASS("3 Pass"),
    RECEIVE_ERRORS("Receive Errors");

    private final String label;

    StatType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
