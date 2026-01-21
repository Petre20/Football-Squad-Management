package UI;

import database.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddMatchDialog extends JDialog {

    private JTextField tfOpponent;
    private JTextField tfDate;
    private JComboBox<String> cbLocation;
    private JTextField tfScored;
    private JTextField tfReceived;

    private boolean matchSaved = false;
    private int matchId = -1; // -1 = Nou, >0 = Editare

    public AddMatchDialog(JFrame parent) {
        super(parent, "Adauga Meci Nou", true);
        this.matchId = -1;
        buildUI();
    }

    // Constructor EDITARE
    public AddMatchDialog(JFrame parent, int id, String opponent, String date, String location, int scored, int received) {
        super(parent, "Editeaza Meci", true);
        this.matchId = id;
        buildUI();

        tfOpponent.setText(opponent);
        tfDate.setText(date);
        cbLocation.setSelectedItem(location);
        tfScored.setText(String.valueOf(scored));
        tfReceived.setText(String.valueOf(received));
    }

    private void buildUI() {
        setSize(400, 350);
        setLocationRelativeTo(getParent());
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Adversar:"));
        tfOpponent = new JTextField();
        add(tfOpponent);

        add(new JLabel("Data (zi.luna.an):"));
        tfDate = new JTextField();
        add(tfDate);

        add(new JLabel("Locatie:"));
        String[] locs = {"Acasa", "Deplasare"};
        cbLocation = new JComboBox<>(locs);
        add(cbLocation);

        add(new JLabel("Goluri Date:"));
        tfScored = new JTextField("0");
        add(tfScored);

        add(new JLabel("Goluri Primite:"));
        tfReceived = new JTextField("0");
        add(tfReceived);

        JButton btnSave = new JButton("Salveaza");
        btnSave.addActionListener(e -> saveMatch());

        JButton btnCancel = new JButton("Anuleaza");
        btnCancel.addActionListener(e -> dispose());

        add(btnCancel);
        add(btnSave);
    }

    private void saveMatch() {
        String opponent = tfOpponent.getText().trim();
        String date = tfDate.getText().trim();
        String location = (String) cbLocation.getSelectedItem();
        String scoredStr = tfScored.getText().trim();
        String receivedStr = tfReceived.getText().trim();

        // 1. Validari de baza (Campuri goale)
        if (opponent.isEmpty() || date.isEmpty() || scoredStr.isEmpty() || receivedStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completeaza toate campurile!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Validare numere (Scor)
        int scored, received;
        try {
            scored = Integer.parseInt(scoredStr);
            received = Integer.parseInt(receivedStr);
            if (scored < 0 || received < 0) {
                JOptionPane.showMessageDialog(this, "Scorul nu poate fi negativ!", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Golurile trebuie sa fie numere (0, 1, 2...)!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String checkDateSql = "SELECT id FROM matches WHERE match_date = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkDateSql)) {
                checkStmt.setString(1, date);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int foundId = rs.getInt("id");
                    // Daca am gasit un meci pe data asta, verificam sa nu fim tot noi (in caz de editare)
                    if (foundId != matchId) {
                        JOptionPane.showMessageDialog(this, "Exista deja un meci programat pe data de " + date + "!", "Conflict Calendar", JOptionPane.WARNING_MESSAGE);
                        return; // Oprim salvarea
                    }
                }
            }

            String sql;
            PreparedStatement pstmt;

            if (matchId == -1) {
                // INSERT
                sql = "INSERT INTO matches (opponent, match_date, location, scored, received) VALUES (?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, opponent);
                pstmt.setString(2, date);
                pstmt.setString(3, location);
                pstmt.setInt(4, scored);
                pstmt.setInt(5, received);
            } else {
                // UPDATE
                sql = "UPDATE matches SET opponent=?, match_date=?, location=?, scored=?, received=? WHERE id=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, opponent);
                pstmt.setString(2, date);
                pstmt.setString(3, location);
                pstmt.setInt(4, scored);
                pstmt.setInt(5, received);
                pstmt.setInt(6, matchId);
            }

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Meci salvat cu succes!");
                matchSaved = true;
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare DB: " + e.getMessage());
        }
    }

    public boolean isMatchSaved() {
        return matchSaved;
    }
}