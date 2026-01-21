package UI;

import database.Database;
import models.TrainingAttendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TrainingAttendanceWindow extends JFrame {

    private int trainingId;
    private JComboBox<PlayerItem> cbPlayers;
    private JComboBox<String> cbStatus;
    private JTextField tfRating;
    private JTable table;
    private DefaultTableModel tableModel;

    // Statusurile posibile
    private static final String[] STATUS_OPTIONS = {"Prezent", "Absent", "Invoit", "Accidentat", "Intarziat"};

    public TrainingAttendanceWindow(int trainingId, String trainingDetails) {
        this.trainingId = trainingId;
        setTitle("Prezenta: " + trainingDetails);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Evalueaza Jucator"));

        // 1. Selectare Jucator
        inputPanel.add(new JLabel("Jucator:"));
        cbPlayers = new JComboBox<>();
        loadPlayersCombo();
        inputPanel.add(cbPlayers);

        // 2. Status (Prezent/Absent etc)
        inputPanel.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(STATUS_OPTIONS);
        inputPanel.add(cbStatus);

        // 3. Randament (Nota 1-10)
        inputPanel.add(new JLabel("Randament (1-10):"));
        tfRating = new JTextField("0");
        // Putem pune un tooltip sa stie userul ca 0 e pentru absenti
        tfRating.setToolTipText("Nota de la 1 la 10. Lasa 0 daca a absentat.");
        inputPanel.add(tfRating);

        // 4. Buton Salvare
        JButton btnSave = new JButton("Salveaza");
        btnSave.setBackground(new Color(200, 255, 200));
        btnSave.addActionListener(e -> saveAttendance());

        // Adaugam un placeholder gol ca sa aliniem butonul
        inputPanel.add(new JLabel(""));
        inputPanel.add(btnSave);

        add(inputPanel, BorderLayout.NORTH);

        String[] columns = {"Jucator", "Status", "Randament"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadAttendanceTable();
    }

    private void loadPlayersCombo() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, first_name, last_name FROM players ORDER BY last_name";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("last_name") + " " + rs.getString("first_name");
                cbPlayers.addItem(new PlayerItem(id, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAttendance() {
        PlayerItem selectedPlayer = (PlayerItem) cbPlayers.getSelectedItem();
        if (selectedPlayer == null) return;

        try {
            int playerId = selectedPlayer.getId();
            String status = (String) cbStatus.getSelectedItem();
            int rating = Integer.parseInt(tfRating.getText().trim());

            if (rating < 0 || rating > 10) {
                JOptionPane.showMessageDialog(this, "Nota trebuie sa fie intre 1 si 10 (sau 0).");
                return;
            }

            try (Connection conn = Database.getConnection()) {
                // Stergem vechea inregistrare daca exista
                String deleteSql = "DELETE FROM training_attendance WHERE training_id=? AND player_id=?";
                PreparedStatement pstmtDel = conn.prepareStatement(deleteSql);
                pstmtDel.setInt(1, trainingId);
                pstmtDel.setInt(2, playerId);
                pstmtDel.executeUpdate();

                // Inseram noua inregistrare
                String insertSql = "INSERT INTO training_attendance (training_id, player_id, status, rating) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(insertSql);
                pstmt.setInt(1, trainingId);
                pstmt.setInt(2, playerId);
                pstmt.setString(3, status);
                pstmt.setInt(4, rating);
                pstmt.executeUpdate();

                loadAttendanceTable(); // Refresh
                JOptionPane.showMessageDialog(this, "Salvat pentru " + selectedPlayer);

                // Resetam doar nota
                tfRating.setText("0");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Randamentul trebuie sa fie un numar!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
        }
    }

    private void loadAttendanceTable() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.last_name, p.first_name, a.* " + // Selectam tot din a (attendance)
                    "FROM training_attendance a " +
                    "JOIN players p ON a.player_id = p.id " +
                    "WHERE a.training_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, trainingId);
            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);
            while(rs.next()) {
                // 1. Cream obiectul
                TrainingAttendance att = new TrainingAttendance(
                        rs.getInt("id"),
                        rs.getInt("training_id"),
                        rs.getInt("player_id"),
                        rs.getString("status"),
                        rs.getInt("rating")
                );

                // 2. Luam numele jucatorului
                String name = rs.getString("last_name") + " " + rs.getString("first_name");

                // 3. Folosim obiectul
                tableModel.addRow(new Object[]{
                        name,
                        att.getStatus(), // Getter
                        att.getRating()  // Getter
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}