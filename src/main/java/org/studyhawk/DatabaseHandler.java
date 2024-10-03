package org.studyhawk;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseHandler {

    public static Properties getDatabaseProperties() {
        Properties props = new Properties();
        String propertyFileName = "database.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            InputStream resourceStream = loader.getResourceAsStream(propertyFileName);
            props.load(resourceStream);
            resourceStream.close();
        } catch (IOException e) {
            System.out.println("[DATABASE] Could not load the database properties file");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("[DATABASE] Could not find database properties file");
            e.printStackTrace();
        }
        return props;
    }

    public static void testConnection() {

        try {

            Properties props = DatabaseHandler.getDatabaseProperties();

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            String query = "SELECT * FROM studyhawk.Cards";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection Established successfully");
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                System.out.println("Term: " + result.getString("term"));
                System.out.println("Definition: " + result.getString("definition"));
            }

            statement.close();
            conn.close();
            System.out.println("Connection Closed....");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
        }

    }

}
