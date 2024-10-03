package org.studyhawk;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Transaction;
import org.studyhawk.Components.*;

/**
 * Class to handle all database transactions.
 */
public class DatabaseHandler {

    /**
     * Gets the database properties file and returns it.
     */
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

    /**
     * Gets a connection to the database and returns it.
     */
    public static Connection getConnection() {
        Connection conn = null; // Connection to build and return

        // Load properties
        Properties props = DatabaseHandler.getDatabaseProperties();
        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        try {

            // Load driver and retrieve connection with credentials
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);

        } catch (SQLException connectionFailed) {
            System.out.println("[ERROR] DATABASE CONNECTION FAILED");
            System.out.println(connectionFailed.getMessage());
        } catch (ClassNotFoundException noDriver) {
            System.out.println("[ERROR] DATABASE DRIVER COULD NOT BE FOUND");
            System.out.println(noDriver.getMessage());
        }

        return conn;

    }

    /**
     * Takes in a connection and closes it.
     * @param conn The connection to close.
     */
    public static void closeConnection(Connection conn) {

        try {
            conn.close();
        } catch (SQLException closeFailed) {
            System.out.println("[ERROR] DATABASE CONNECTION FAILED TO CLOSE");
            System.out.println(closeFailed.getMessage());
        }

    }

    // public static void testConnection() {

    //     try {

    //         Properties props = DatabaseHandler.getDatabaseProperties();

    //         String url = props.getProperty("db.url");
    //         String username = props.getProperty("db.username");
    //         String password = props.getProperty("db.password");
    //         String query = "SELECT * FROM studyhawk.Cards";
    //         Class.forName("com.mysql.cj.jdbc.Driver");
    //         Connection conn = DriverManager.getConnection(url, username, password);
    //         System.out.println("Connection Established successfully");
    //         Statement statement = conn.createStatement();
    //         ResultSet result = statement.executeQuery(query);
    //         while (result.next()) {
    //             System.out.println("Term: " + result.getString("term"));
    //             System.out.println("Definition: " + result.getString("definition"));
    //         }

    //         statement.close();
    //         conn.close();
    //         System.out.println("Connection Closed....");

    //     } catch (SQLException e) {
    //         System.out.println(e.getMessage());
    //         e.printStackTrace(System.out);
    //     } catch (ClassNotFoundException e) {
    //         System.out.println(e.getMessage());
    //         e.printStackTrace(System.out);
    //     }

    // }

    /**
     * Gets all decks from the database.
     */
    public static ArrayList<Deck> getDecks() {

        ArrayList<Deck> decks = new ArrayList<>();

        Connection conn = DatabaseHandler.getConnection();

        // Get decks from database
        try {

            // Query deck data from database
            String querySQL = "SELECT * FROM studyhawk.Decks";
            PreparedStatement statement = conn.prepareStatement(querySQL);
            ResultSet result = statement.executeQuery();

            // Place all deck data in ArrayList
            try {
                while (result.next()) {
                    Deck deck = new Deck(
                        result.getString("title"),
                        result.getString("description"),
                        result.getBoolean("favorite")
                    );
                    deck.setDeckID(result.getInt("deckID"));
                    decks.add(deck);
                }

            } catch (SQLException parsingFailed) {
                System.out.println("[ERROR] DECKS PROCESSING FAILED");
                System.out.println(parsingFailed.getMessage());
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DECKS QUERY FAILED");
            System.out.println(queryFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);
        return decks;

    }

}
