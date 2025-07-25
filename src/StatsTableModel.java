import javax.swing.table.DefaultTableModel;
import java.util.Map;

public class StatsTableModel extends DefaultTableModel {

    public StatsTableModel() {
        for (int i = 0; i < getColumnCount(); i++) {
            addColumn(getColumnName(i));
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void updateData(Map<String, Map<String, Integer>> data) {
        setRowCount(0);
        setColumnCount(0);
        addColumn("Player");
        for (StatType stat : StatType.values()) {
            addColumn(stat.getLabel());
        }

        for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
            Object[] row = new Object[getColumnCount()];
            row[0] = entry.getKey();
            for (int i = 1; i < row.length; i++) {
                row[i] = entry.getValue().getOrDefault(getColumnName(i), 0);
            }
            addRow(row);
        }
    }
}
