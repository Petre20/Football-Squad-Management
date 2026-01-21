package UI;

import database.Database;
import models.Training;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TrainingsWindow extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public TrainingsWindow() {
        setTitle("Program Antrenamente");
        setSize(800, 500);
        setLocationRelativeTo(null);

        // 1. Configuram coloanele
        String[] columns = {"ID", "Data", "Tip", "Durata (min)", "Descriere"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 2. Butoane
        JPanel bottomPanel = new JPanel();
        JButton btnAdd = new JButton("Programeaza Antrenament");
        JButton btnAttendance = new JButton("Prezenta & Randament");
        btnAttendance.setBackground(new Color(255, 255, 200));
        JButton btnDelete = new JButton("Anuleaza Antrenament");

        // Actiuni butoane
        btnAdd.addActionListener(e -> {
            AddTrainingDialog dialog = new AddTrainingDialog(this);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadTrainingsData();
            }
        });

        btnAttendance.addActionListener(e -> openAttendanceWindow());
        btnDelete.addActionListener(e -> deleteSelectedTraining());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnAttendance);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);

        // 3. Incarcam datele
        loadTrainingsData();
    }

    private void deleteSelectedTraining() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un antrenament pentru a-l sterge!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String date = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Sigur anulezi antrenamentul din " + date + "?",
                "Confirmare", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                String sql = "DELETE FROM trainings WHERE id=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                loadTrainingsData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openAttendanceWindow() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un antrenament din lista!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String date = (String) tableModel.getValueAt(row, 1);
        String type = (String) tableModel.getValueAt(row, 2);

        TrainingAttendanceWindow attWin = new TrainingAttendanceWindow(id, type + " (" + date + ")");
        attWin.setVisible(true);
    }

    private void loadTrainingsData() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM trainings ORDER BY STR_TO_DATE(training_date, '%d.%m.%Y') DESC")) {

            tableModel.setRowCount(0);

            while (rs.next()) {
                Training t = new Training(
                        rs.getInt("id"),
                        rs.getString("training_date"),
                        rs.getString("type"),
                        rs.getInt("duration"),
                        rs.getString("description")
                );

                Object[] row = {
                        t.getId(),
                        t.getDate(),
                        t.getType(),
                        t.getDuration(),
                        t.getDescription()
                };
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
        }
    }
}