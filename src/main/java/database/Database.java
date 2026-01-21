package database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Database {

    public static Connection getConnection() {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream("src/main/resources/config.properties");
            props.load(in);
            in.close();

            String url = "jdbc:mysql://localhost:3306/football_db";
            String user = props.getProperty("username");
            String pass = props.getProperty("password");

            return DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            System.out.println("Eroare la conectare DB: " + e.getMessage());
            return null;
        }
    }
}