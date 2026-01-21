package UI;

import database.Database;
import models.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PlayersWindow extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public PlayersWindow() {
        setTitle("Gestiune Jucatori");
        setSize(750, 500);
        setLocationRelativeTo(null);

        // 1. Configurare Tabel
        String[] columns = {"ID", "Nume", "Prenume", "Pozitie", "Numar"};

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
        JButton btnAdd = new JButton("Adauga Jucator");
        JButton btnEdit = new JButton("Editeaza");
        JButton btnDelete = new JButton("Sterge");

        btnAdd.addActionListener(e -> {
            AddPlayerDialog dialog = new AddPlayerDialog(this);
            dialog.setVisible(true);
            if (dialog.isPlayerAdded()) {
                loadPlayersData();
            }
        });

        btnEdit.addActionListener(e -> editSelectedPlayer());
        btnDelete.addActionListener(e -> deleteSelectedPlayer());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);

        // 3. Incarcare date
        loadPlayersData();
    }

    private void loadPlayersData() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM players")) {

            tableModel.setRowCount(0);

            while (rs.next()) {
                // 1. Luam datele din SQL si cream un obiect Player
                Player p = new Player(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("position"),
                        rs.getInt("number")
                );

                Object[] row = {
                        p.getId(),
                        p.getLastName(),    // Folosim getter
                        p.getFirstName(),   // Folosim getter
                        p.getPosition(),    // Folosim getter
                        p.getNumber()       // Folosim getter
                };

                tableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare incarcare: " + e.getMessage());
        }
    }

    private void deleteSelectedPlayer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un jucator!");
            return;
        }

        // Luam ID-ul si Numele din tabel pentru confirmare
        int id = (int) tableModel.getValueAt(row, 0);
        String nume = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Sigur stergi pe " + nume + "?",
                "Confirmare", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                String sql = "DELETE FROM players WHERE id=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                loadPlayersData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void editSelectedPlayer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecteaza un jucator!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String nume = (String) tableModel.getValueAt(row, 1);
        String prenume = (String) tableModel.getValueAt(row, 2);
        String pozitie = (String) tableModel.getValueAt(row, 3);
        int numar = (int) tableModel.getValueAt(row, 4);

        AddPlayerDialog dialog = new AddPlayerDialog(this, id, nume, prenume, pozitie, numar);
        dialog.setVisible(true);

        if (dialog.isPlayerAdded()) {
            loadPlayersData();
        }
    }
}