package UI;

import database.Database;
import models.Match;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MatchesWindow extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public MatchesWindow() {
        setTitle("Meciuri si Rezultate");
        setSize(700, 500);
        setLocationRelativeTo(null);

        String[] columns = {"ID", "Adversar", "Data", "Locatie", "Goluri date", "Goluri primite"};

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

        JPanel bottomPanel = new JPanel();
        JButton btnAdd = new JButton("Adauga Meci");
        JButton btnEdit = new JButton("Editeaza");
        JButton btnDelete = new JButton("Sterge Meci");
        JButton btnStats = new JButton("Statistici Jucatori");


        // ADAUGA
        btnAdd.addActionListener(e -> {
            AddMatchDialog dialog = new AddMatchDialog(this);
            dialog.setVisible(true);
            if (dialog.isMatchSaved()) {
                loadMatchesData();
            }
        });

        // EDITEAZA
        btnEdit.addActionListener(e -> editSelectedMatch());

        // STERGE
        btnDelete.addActionListener(e -> deleteSelectedMatch());

        // STATISTICI JUCATORI
        btnStats.addActionListener(e -> openStatsWindow());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnStats);

        add(bottomPanel, BorderLayout.SOUTH);

        // Incarcare date
        loadMatchesData();
    }

    private void editSelectedMatch() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un meci pentru editare!");
            return;
        }

        // Luam datele din tabel pentru a popula formularul
        int id = (int) tableModel.getValueAt(row, 0);
        String opponent = (String) tableModel.getValueAt(row, 1);
        String date = (String) tableModel.getValueAt(row, 2);
        String location = (String) tableModel.getValueAt(row, 3);
        int scored = (int) tableModel.getValueAt(row, 4);
        int received = (int) tableModel.getValueAt(row, 5);

        // Deschidem dialogul cu datele completate
        AddMatchDialog dialog = new AddMatchDialog(this, id, opponent, date, location, scored, received);
        dialog.setVisible(true);

        if (dialog.isMatchSaved()) {
            loadMatchesData();
        }
    }

    private void deleteSelectedMatch() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un meci pentru a-l sterge!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String opponent = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Sigur stergi meciul cu " + opponent + "?",
                "Confirmare", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                String sql = "DELETE FROM matches WHERE id=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();

                loadMatchesData(); // Refresh la tabel

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Eroare la stergere: " + ex.getMessage());
            }
        }
    }

    private void loadMatchesData() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM matches ORDER BY STR_TO_DATE(match_date, '%d.%m.%Y') DESC")) {

            tableModel.setRowCount(0);

            while (rs.next()) {
                Match m = new Match(
                        rs.getInt("id"),
                        rs.getString("opponent"),
                        rs.getString("match_date"),
                        rs.getString("location"),
                        rs.getInt("scored"),
                        rs.getInt("received")
                );

                Object[] row = {
                        m.getId(),
                        m.getOpponent(),
                        m.getDate(),
                        m.getLocation(),
                        m.getScored(),
                        m.getReceived()
                };
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare incarcare: " + e.getMessage());
        }
    }

    private void openStatsWindow() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un meci pentru a adauga statistici!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String opponent = (String) tableModel.getValueAt(row, 1);
        String date = (String) tableModel.getValueAt(row, 2);

        MatchStatsWindow statsWin = new MatchStatsWindow(id, opponent + " (" + date + ")");
        statsWin.setVisible(true);
    }
}