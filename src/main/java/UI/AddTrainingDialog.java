package UI;

import database.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddTrainingDialog extends JDialog {

    private JTextField tfDate;
    private JComboBox<String> cbType;
    private JTextField tfDuration;
    private JTextArea taDescription;
    private boolean saved = false;

    private static final String[] TYPES = {
            "Fizic / Cardio",
            "Tactic",
            "Tehnic",
            "Recuperare / Refacere",
            "Sala de Forta"
    };

    public AddTrainingDialog(JFrame parent) {
        super(parent, "Programeaza Antrenament", true);
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Data
        formPanel.add(new JLabel("Data (zi.luna.an):"));
        tfDate = new JTextField();
        formPanel.add(tfDate);

        // 2. Tip (Dropdown)
        formPanel.add(new JLabel("Tip Antrenament:"));
        cbType = new JComboBox<>(TYPES);
        formPanel.add(cbType);

        // 3. Durata
        formPanel.add(new JLabel("Durata (minute):"));
        tfDuration = new JTextField("90"); // Default 90 min
        formPanel.add(tfDuration);

        formPanel.add(new JLabel("Descriere / Note:"));
        taDescription = new JTextArea(3, 20);
        taDescription.setLineWrap(true);
        JScrollPane scrollDesc = new JScrollPane(taDescription);

        add(formPanel, BorderLayout.NORTH);
        add(scrollDesc, BorderLayout.CENTER);

        // Butoane
        JPanel btnPanel = new JPanel();
        JButton btnSave = new JButton("Salveaza");
        JButton btnCancel = new JButton("Anuleaza");

        btnSave.addActionListener(e -> saveTraining());
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void saveTraining() {
        String date = tfDate.getText().trim();
        String type = (String) cbType.getSelectedItem();
        String durStr = tfDuration.getText().trim();
        String desc = taDescription.getText().trim();

        if (date.isEmpty() || durStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data si Durata sunt obligatorii!");
            return;
        }

        try {
            int duration = Integer.parseInt(durStr);
            if (duration <= 0) {
                JOptionPane.showMessageDialog(this, "Durata trebuie sa fie pozitiva!");
                return;
            }

            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO trainings (training_date, type, duration, description) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, date);
                pstmt.setString(2, type);
                pstmt.setInt(3, duration);
                pstmt.setString(4, desc);

                pstmt.executeUpdate();
                saved = true;
                dispose();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Durata trebuie sa fie un numar (ex: 90)!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }
}