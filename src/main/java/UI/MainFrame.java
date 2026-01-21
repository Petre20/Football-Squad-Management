package UI;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        // 1. Setari de baza ale ferestrei
        setTitle("Football Team Manager");
        setSize(600, 400); // Dimensiunea ferestrei
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Inchide aplicatia la X
        setLocationRelativeTo(null); // Centreaza fereastra pe ecran

        // 2. Creare panou principal (Dashboard)
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 randuri, spatiu intre ele

        // 3. Adaugare butoane de navigare
        JButton btnPlayers = new JButton("Gestiune Jucatori");
        // Cand dam click, deschidem fereastra cu jucatori
        btnPlayers.addActionListener(e -> {
            PlayersWindow playersWin = new PlayersWindow();
            playersWin.setVisible(true);
        });

        JButton btnMatches = new JButton("Meciuri & Rezultate");
        btnMatches.addActionListener(e -> {
            MatchesWindow matchesWin = new MatchesWindow();
            matchesWin.setVisible(true);
        });

        JButton btnTraining = new JButton("Antrenamente");
        btnTraining.addActionListener(e -> {
            TrainingsWindow trainWin = new TrainingsWindow();
            trainWin.setVisible(true);
        });

        JButton btnReports = new JButton("Rapoarte");
        btnReports.addActionListener(e -> {
            ReportsWindow repWin = new ReportsWindow();
            repWin.setVisible(true);
        });

        Font btnFont = new Font("Arial", Font.BOLD, 18);
        btnPlayers.setFont(btnFont);
        btnMatches.setFont(btnFont);
        btnTraining.setFont(btnFont);
        btnReports.setFont(btnFont);

        // Adaugam butoanele pe panou
        dashboardPanel.add(btnPlayers);
        dashboardPanel.add(btnMatches);
        dashboardPanel.add(btnTraining);
        dashboardPanel.add(btnReports);

        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        add(dashboardPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Panou de Control", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
    }
}