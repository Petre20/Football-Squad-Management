package org.example;

import UI.MainFrame;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class App {
    public static void main(String[] args) {
        //stDatabaseConnection();
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }


    /*private static void testDatabaseConnection() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(fileInputStream);
            String dbUrl = "jdbc:mysql://localhost:3306/football_db";
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
                System.out.println("DB Connected Succesfully!");
            }
        } catch (Exception e) {
            System.out.println("DB Connection Failed: " + e.getMessage());
        }
    }*/
}