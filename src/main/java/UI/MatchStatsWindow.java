package UI;

import database.Database;
import models.MatchStats;
import models.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MatchStatsWindow extends JFrame {

    private int matchId; // Stim pentru ce meci adaugam statistici
    private JComboBox<PlayerItem> cbPlayers;

    // Campurile pentru statistici
    private JTextField tfGoals, tfShotsOnTarget, tfTotalShots;
    private JTextField tfPassesOk, tfPassesTotal;
    private JTextField tfDribbles, tfDistance, tfSaves, tfTackles;

    private JTable statsTable;
    private DefaultTableModel tableModel;

    public MatchStatsWindow(int matchId, String matchDetails) {
        this.matchId = matchId;
        setTitle("Statistici: " + matchDetails);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Adauga Performanta Jucator"));

        // 1. Selectare Jucator
        inputPanel.add(new JLabel("Selecteaza Jucator:"));
        cbPlayers = new JComboBox<>();
        loadPlayersCombo(); // Incarcam jucatorii in lista
        inputPanel.add(cbPlayers);

        // Spatiu gol pentru aliniere
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));

        // 2. Campuri Statistici
        inputPanel.add(new JLabel("Goluri:"));
        tfGoals = new JTextField("0"); inputPanel.add(tfGoals);

        inputPanel.add(new JLabel("Suturi pe Poarta:"));
        tfShotsOnTarget = new JTextField("0"); inputPanel.add(tfShotsOnTarget);

        inputPanel.add(new JLabel("Total Suturi:"));
        tfTotalShots = new JTextField("0"); inputPanel.add(tfTotalShots);

        inputPanel.add(new JLabel("Pase Reusite:"));
        tfPassesOk = new JTextField("0"); inputPanel.add(tfPassesOk);

        inputPanel.add(new JLabel("Pase Totale:"));
        tfPassesTotal = new JTextField("0"); inputPanel.add(tfPassesTotal);

        inputPanel.add(new JLabel("Driblinguri:"));
        tfDribbles = new JTextField("0"); inputPanel.add(tfDribbles);

        inputPanel.add(new JLabel("Distanta (km):"));
        tfDistance = new JTextField("0.0"); inputPanel.add(tfDistance);

        inputPanel.add(new JLabel("Interventii (GK):"));
        tfSaves = new JTextField("0"); inputPanel.add(tfSaves);

        inputPanel.add(new JLabel("Deposedari:"));
        tfTackles = new JTextField("0"); inputPanel.add(tfTackles);

        // Buton Salvare
        JButton btnSave = new JButton("Salveaza Statistici");
        btnSave.setBackground(new Color(200, 255, 200));
        btnSave.addActionListener(e -> saveStats());
        inputPanel.add(btnSave);

        add(inputPanel, BorderLayout.NORTH);

        String[] columns = {"Jucator", "Goluri", "Sut(Poarta/Total)", "Pase(Reusite/Total)", "Driblinguri", "Dist(km)", "Interventii salvatoare(GK)", "Deposedari"};
        tableModel = new DefaultTableModel(columns, 0);
        statsTable = new JTable(tableModel);
        add(new JScrollPane(statsTable), BorderLayout.CENTER);

        loadStatsTable();
    }

    private void loadPlayersCombo() {
        try (Connection conn = Database.getConnection()) {
            // Luam toti jucatorii sa ii punem in dropdown
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

    private void saveStats() {
        PlayerItem selectedPlayer = (PlayerItem) cbPlayers.getSelectedItem();
        if (selectedPlayer == null) return;

        try {
            int playerId = selectedPlayer.getId();

            // Validare a numerelor
            int goals = Integer.parseInt(tfGoals.getText().trim());
            int sTarget = Integer.parseInt(tfShotsOnTarget.getText().trim());
            int sTotal = Integer.parseInt(tfTotalShots.getText().trim());
            int pOk = Integer.parseInt(tfPassesOk.getText().trim());
            int pTotal = Integer.parseInt(tfPassesTotal.getText().trim());
            int dribbles = Integer.parseInt(tfDribbles.getText().trim());
            double dist = Double.parseDouble(tfDistance.getText().trim());
            int saves = Integer.parseInt(tfSaves.getText().trim());
            int tackles = Integer.parseInt(tfTackles.getText().trim());

            try (Connection conn = Database.getConnection()) {
                // Verificam daca exista deja statistici pentru acest jucator la acest meci
                // Daca da, facem UPDATE, daca nu, INSERT.

                String deleteOld = "DELETE FROM match_stats WHERE match_id=? AND player_id=?";
                PreparedStatement pstmtDel = conn.prepareStatement(deleteOld);
                pstmtDel.setInt(1, matchId);
                pstmtDel.setInt(2, playerId);
                pstmtDel.executeUpdate();

                String insertSql = "INSERT INTO match_stats " +
                        "(match_id, player_id, goals, shots_on_target, total_shots, passes_completed, passes_total, dribbles_completed, distance_km, saves, tackles) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement pstmt = conn.prepareStatement(insertSql);
                pstmt.setInt(1, matchId);
                pstmt.setInt(2, playerId);
                pstmt.setInt(3, goals);
                pstmt.setInt(4, sTarget);
                pstmt.setInt(5, sTotal);
                pstmt.setInt(6, pOk);
                pstmt.setInt(7, pTotal);
                pstmt.setInt(8, dribbles);
                pstmt.setDouble(9, dist);
                pstmt.setInt(10, saves);
                pstmt.setInt(11, tackles);

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Statistici salvate pentru " + selectedPlayer);
                loadStatsTable(); // Refresh la tabelul de jos

                // Resetam campurile la 0 pentru urmatorul jucator
                resetFields();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Toate campurile trebuie sa fie numere! (Distanta poate fi ex: 10.5)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Eroare: " + ex.getMessage());
        }
    }

    private void loadStatsTable() {
        try (Connection conn = Database.getConnection()) {
            // Selectam tot ce avem nevoie
            String sql = "SELECT p.last_name, p.first_name, s.* FROM match_stats s " +
                    "JOIN players p ON s.player_id = p.id " +
                    "WHERE s.match_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);
            while(rs.next()) {
                // 1. Cream obiectul MatchStats
                MatchStats stats = new MatchStats(
                        rs.getInt("id"),
                        rs.getInt("match_id"),
                        rs.getInt("player_id"),
                        rs.getInt("goals"),
                        rs.getInt("shots_on_target"),
                        rs.getInt("total_shots"),
                        rs.getInt("passes_completed"),
                        rs.getInt("passes_total"),
                        rs.getInt("dribbles_completed"),
                        rs.getDouble("distance_km"),
                        rs.getInt("saves"),
                        rs.getInt("tackles")
                );

                // 2. Luam numele separat (pentru ca MatchStats tine doar ID-ul jucatorului)
                String playerName = rs.getString("last_name") + " " + rs.getString("first_name");

                // 3. Formatam datele pentru tabel folosind GETTERII din obiect
                String shotsDisplay = stats.getShotsOnTarget() + "/" + stats.getTotalShots();
                String passesDisplay = stats.getPassesCompleted() + "/" + stats.getPassesTotal();

                Object[] row = {
                        playerName,
                        stats.getGoals(),
                        shotsDisplay,
                        passesDisplay,
                        stats.getDribblesCompleted(),
                        stats.getDistanceKm(),
                        stats.getSaves(),
                        stats.getTackles()
                };

                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetFields() {
        tfGoals.setText("0");
        tfShotsOnTarget.setText("0");
        tfTotalShots.setText("0");
        tfPassesOk.setText("0");
        tfPassesTotal.setText("0");
        tfDribbles.setText("0");
        tfDistance.setText("0.0");
        tfSaves.setText("0");
        tfTackles.setText("0");
    }
}