package UI;

import database.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

public class AddPlayerDialog extends JDialog {

    private JTextField tfFirstName;
    private JTextField tfLastName;
    private JComboBox<String> cbPosition;
    private JTextField tfNumber;
    private boolean playerAdded = false;
    private int playerId = -1;

    // Lista fixa de pozitii acceptate
    private static final String[] VALID_POSITIONS = {
            "Portar",
            "Fundas Stanga", "Fundas Dreapta", "Fundas Central",
            "Mijlocas Stanga", "Mijlocas Dreapta", "Mijlocas Central",
            "Mijlocas Ofensiv", "Mijlocas Defensiv",
            "Atacant Stanga", "Atacant Dreapta", "Atacant Central"
    };

    public AddPlayerDialog(JFrame parent) {
        super(parent, "Adauga Jucator Nou", true);
        this.playerId = -1;
        buildUI();
    }

    public AddPlayerDialog(JFrame parent, int id, String lastName, String firstName, String position, int number) {
        super(parent, "Editeaza Jucator", true);
        this.playerId = id;
        buildUI();

        tfLastName.setText(lastName);
        tfFirstName.setText(firstName);
        // Setam valoarea selectata in Dropdown
        cbPosition.setSelectedItem(position);
        tfNumber.setText(String.valueOf(number));
    }

    private void buildUI() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Nume (Last Name):"));
        tfLastName = new JTextField();
        add(tfLastName);

        add(new JLabel("Prenume (First Name):"));
        tfFirstName = new JTextField();
        add(tfFirstName);

        add(new JLabel("Pozitie:"));
        cbPosition = new JComboBox<>(VALID_POSITIONS);
        add(cbPosition);

        add(new JLabel("Numar Tricou (1-99):"));
        tfNumber = new JTextField();
        add(tfNumber);

        JButton btnSave = new JButton("Salveaza");
        btnSave.addActionListener(e -> savePlayer());

        JButton btnCancel = new JButton("Anuleaza");
        btnCancel.addActionListener(e -> dispose());

        add(btnCancel);
        add(btnSave);
    }

    private void savePlayer() {
        String firstName = tfFirstName.getText().trim();
        String lastName = tfLastName.getText().trim();
        String position = (String) cbPosition.getSelectedItem();
        String numberStr = tfNumber.getText().trim();

        // 1. Validare campuri goale
        if (firstName.isEmpty() || lastName.isEmpty() || numberStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Toate campurile sunt obligatorii!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String textPattern = "^[a-zA-ZăâîșțĂÂÎȘȚ -]+$";

        if (!Pattern.matches(textPattern, firstName)) {
            JOptionPane.showMessageDialog(this, "Prenumele contine caractere invalide! Folositi doar litere.", "Eroare Format", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!Pattern.matches(textPattern, lastName)) {
            JOptionPane.showMessageDialog(this, "Numele contine caractere invalide! Folositi doar litere.", "Eroare Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Validare NUMAR (Intre 1 si 99)
        int number;
        try {
            number = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Numarul de pe tricou trebuie sa fie numeric!", "Eroare Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (number < 1 || number > 99) {
            JOptionPane.showMessageDialog(this, "Numarul tricoului trebuie sa fie intre 1 si 99!", "Eroare Interval", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection()) {

            // 4. Validare Nume DUPLICAT
            String checkNameSql = "SELECT id FROM players WHERE LOWER(first_name) = LOWER(?) AND LOWER(last_name) = LOWER(?)";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkNameSql)) {
                checkStmt.setString(1, firstName);
                checkStmt.setString(2, lastName);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int foundId = rs.getInt("id");
                    if (foundId != playerId) {
                        JOptionPane.showMessageDialog(this, "Jucatorul " + firstName + " " + lastName + " exista deja!", "Duplicat", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // 5. Validare Numar DUPLICAT
            String checkNumSql = "SELECT id, first_name, last_name FROM players WHERE number = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkNumSql)) {
                checkStmt.setInt(1, number);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int foundId = rs.getInt("id");
                    if (foundId != playerId) {
                        String owner = rs.getString("first_name") + " " + rs.getString("last_name");
                        JOptionPane.showMessageDialog(this, "Numarul " + number + " este deja folosit de " + owner + "!", "Numar Indisponibil", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // --- SALVAREA EFECTIVA ---
            String sql;
            PreparedStatement pstmt;

            if (playerId == -1) {
                // INSERT
                sql = "INSERT INTO players (first_name, last_name, position, number) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, position);
                pstmt.setInt(4, number);
            } else {
                // UPDATE
                sql = "UPDATE players SET first_name=?, last_name=?, position=?, number=? WHERE id=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, position);
                pstmt.setInt(4, number);
                pstmt.setInt(5, playerId);
            }

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Salvare reusita!");
                playerAdded = true;
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare tehnica: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isPlayerAdded() {
        return playerAdded;
    }
}