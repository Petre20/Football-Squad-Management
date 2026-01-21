package UI;

import database.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportsWindow extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public ReportsWindow() {
        setTitle("Rapoarte si Statistici");
        setSize(750, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Bara de meniu cu butoane
        JPanel menuPanel = new JPanel();

        JButton btnScorers = new JButton("Top Marcatori");
        JButton btnPassers = new JButton("Top Pasatori");
        JButton btnDefenders = new JButton("Top Aparatori");
        JButton btnGoalkeepers = new JButton("Top Portari");
        JButton btnTraining = new JButton("Top Antrenament");

        // Stilizare simpla (Culori diferite pentru categorii)
        btnScorers.setBackground(new Color(220, 255, 220));
        btnPassers.setBackground(new Color(220, 255, 220));

        btnDefenders.setBackground(new Color(255, 240, 200));
        btnGoalkeepers.setBackground(new Color(255, 240, 200));

        btnTraining.setBackground(new Color(220, 230, 255));

        // Actiuni
        btnScorers.addActionListener(e -> showTopScorers());
        btnPassers.addActionListener(e -> showTopPassers());
        btnDefenders.addActionListener(e -> showTopDefenders());
        btnGoalkeepers.addActionListener(e -> showTopGoalkeepers());
        btnTraining.addActionListener(e -> showTrainingReport());

        menuPanel.add(btnScorers);
        menuPanel.add(btnPassers);
        menuPanel.add(btnDefenders);
        menuPanel.add(btnGoalkeepers);
        menuPanel.add(btnTraining);

        add(menuPanel, BorderLayout.NORTH);

        // 2. Tabelul
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Incarcam implicit primul raport
        showTopScorers();
    }

    // --- RAPORT 1: TOP MARCATORI ---
    private void showTopScorers() {
        setColumns(new String[]{"Loc", "Nume Jucator", "Goluri Totale", "Meciuri Jucate"});

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.first_name, p.last_name, SUM(s.goals) as total_goals, COUNT(s.match_id) as matches " +
                    "FROM match_stats s " +
                    "JOIN players p ON s.player_id = p.id " +
                    "GROUP BY p.id " +
                    "ORDER BY total_goals DESC";

            fillTable(conn, sql, "total_goals", "matches");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- RAPORT 2: TOP PASATORI ---
    private void showTopPassers() {
        setColumns(new String[]{"Loc", "Nume Jucator", "Pase Reusite", "Total Pase"});

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.first_name, p.last_name, SUM(s.passes_completed) as total_ok, SUM(s.passes_total) as total_all " +
                    "FROM match_stats s " +
                    "JOIN players p ON s.player_id = p.id " +
                    "GROUP BY p.id " +
                    "ORDER BY total_ok DESC";

            fillTable(conn, sql, "total_ok", "total_all");
        } catch (Exception e) { e.printStackTrace(); }
    }

    //RAPORT 3: TOP APARATORI (Deposedari)
    private void showTopDefenders() {
        setColumns(new String[]{"Loc", "Nume Jucator", "Deposedari (Tackles)", "Meciuri Jucate"});

        try (Connection conn = Database.getConnection()) {
            // Selectam suma deposedarilor (tackles)
            String sql = "SELECT p.first_name, p.last_name, SUM(s.tackles) as total_tackles, COUNT(s.match_id) as matches " +
                    "FROM match_stats s " +
                    "JOIN players p ON s.player_id = p.id " +
                    "GROUP BY p.id " +
                    "ORDER BY total_tackles DESC";

            fillTable(conn, sql, "total_tackles", "matches");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- RAPORT 4: TOP PORTARI (Interventii) --- (NOU)
    private void showTopGoalkeepers() {
        setColumns(new String[]{"Loc", "Nume Jucator", "Interventii (Saves)", "Meciuri Jucate"});

        try (Connection conn = Database.getConnection()) {
            // Selectam suma interventiilor (saves)
            String sql = "SELECT p.first_name, p.last_name, SUM(s.saves) as total_saves, COUNT(s.match_id) as matches " +
                    "FROM match_stats s " +
                    "JOIN players p ON s.player_id = p.id " +
                    "GROUP BY p.id " +
                    // Putem filtra optional doar portarii, dar e ok si asa, ca restul au 0 saves
                    "ORDER BY total_saves DESC";

            fillTable(conn, sql, "total_saves", "matches");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- RAPORT 5: PREZENTA SI NOTE ---
    private void showTrainingReport() {
        setColumns(new String[]{"Loc", "Nume Jucator", "Prezente", "Nota Medie"});

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.first_name, p.last_name, " +
                    "COUNT(CASE WHEN a.status IN ('Prezent', 'Intarziat') THEN 1 END) as presence_count, " +
                    "AVG(a.rating) as avg_rating " +
                    "FROM training_attendance a " +
                    "JOIN players p ON a.player_id = p.id " +
                    "GROUP BY p.id " +
                    "ORDER BY avg_rating DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            int rank = 1;
            while (rs.next()) {
                String name = rs.getString("last_name") + " " + rs.getString("first_name");
                int count = rs.getInt("presence_count");
                double avg = rs.getDouble("avg_rating");
                String avgStr = String.format("%.2f", avg);

                tableModel.addRow(new Object[]{rank++, name, count, avgStr});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    //Metoda ajutatoare pentru a nu repeta codul de umplere tabel
    private void fillTable(Connection conn, String sql, String col1Name, String col2Name) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        int rank = 1;
        while (rs.next()) {
            String name = rs.getString("last_name") + " " + rs.getString("first_name");
            int val1 = rs.getInt(col1Name);
            int val2 = rs.getInt(col2Name);

            // Afisam doar daca jucatorul are macar o statistica > 0 (optional)
            if (val1 > 0 || val2 > 0) {
                tableModel.addRow(new Object[]{rank++, name, val1, val2});
            }
        }
    }

    private void setColumns(String[] columns) {
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
    }
}