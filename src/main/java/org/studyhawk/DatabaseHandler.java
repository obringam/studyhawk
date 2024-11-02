package org.studyhawk;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

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

    public static Deck getDeckByID(int ID) {

        Connection conn = DatabaseHandler.getConnection();

        // Get deck from database
        try {
            String querySQL = "SELECT * FROM studyhawk.Decks WHERE deckID = (?)";
            PreparedStatement statement = conn.prepareStatement(querySQL);

            statement.setInt(1, ID);

            ResultSet result = statement.executeQuery();

            // Create deck object
            try {
                result.next();
                Deck deck = new Deck(
                    result.getString("title"),
                    result.getString("description"),
                    result.getBoolean("favorite")
                );
                deck.setDeckID(result.getInt("deckID"));
                DatabaseHandler.closeConnection(conn);
                return deck;

            } catch (SQLException parsingFailed) {
                System.out.println("[ERROR] DECK PROCESSING FAILED");
                System.out.println(parsingFailed.getMessage());
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DECK QUERY FAILED");
            System.out.println(queryFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);
        return null;

    }

    /**
     * Gets all cards from the database.
     */
    public static ArrayList<Card> getCards() {

        ArrayList<Card> cards = new ArrayList<>();

        Connection conn = DatabaseHandler.getConnection();

        // Get cards from database
        try {

            // Query card data from database
            String querySQL = "SELECT * FROM studyhawk.Cards";
            PreparedStatement statement = conn.prepareStatement(querySQL);
            ResultSet result = statement.executeQuery();

            // Place all card data in ArrayList
            try {
                while (result.next()) {
                    Card card = new Card(
                        result.getInt("deckID"),
                        result.getString("term"),
                        result.getString("definition"),
                        result.getBoolean("favorite")
                    );
                    card.setCardID(result.getInt("cardID"));
                    cards.add(card);
                }

            } catch (SQLException parsingFailed) {
                System.out.println("[ERROR] CARDS PROCESSING FAILED");
                System.out.println(parsingFailed.getMessage());
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] CARDS QUERY FAILED");
            System.out.println(queryFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);
        return cards;

    }

    /**
     * Gets all cards from the database from a certain deck.
     * @param deckID The ID of the deck to get the cards from
     * @return ArrayList of Cards from the deck
     */
    public static ArrayList<Card> getCardsByDeckID(int deckID) {

        ArrayList<Card> cards = new ArrayList<>();

        Connection conn = DatabaseHandler.getConnection();

        // Get cards from database
        try {

            // Query card data from database
            String querySQL = "SELECT * FROM studyhawk.Cards WHERE deckID = ?";
            PreparedStatement statement = conn.prepareStatement(querySQL);

            statement.setInt(1, deckID);

            ResultSet result = statement.executeQuery();

            // Place all card data in ArrayList
            try {
                while (result.next()) {
                    Card card = new Card(
                        result.getInt("deckID"),
                        result.getString("term"),
                        result.getString("definition"),
                        result.getBoolean("favorite")
                    );
                    card.setCardID(result.getInt("cardID"));
                    cards.add(card);
                }

            } catch (SQLException parsingFailed) {
                System.out.println("[ERROR] CARDS PROCESSING FAILED");
                System.out.println(parsingFailed.getMessage());
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] CARDS QUERY FAILED");
            System.out.println(queryFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);
        return cards;

    }

    /**
     * Gets a user from the database with a certain username.
     * @param username The username of the user
     * @return The user object
     */
    public static UserAccount getUserByUsername(String username) {

        Connection conn = DatabaseHandler.getConnection();

        // Get user from database
        try {
            String querySQL = "SELECT * FROM studyhawk.Users WHERE username = (?)";
            PreparedStatement statement = conn.prepareStatement(querySQL);

            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            UserAccount user = null;
            if (result.next()) {
                user = new UserAccount(
                    result.getString("username"),
                    result.getString("email"),
                    result.getString("password")
                );
                user.setUserID(result.getInt("userID"));
            }
            DatabaseHandler.closeConnection(conn);
            return user;


        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] USER QUERY FAILED");
            System.out.println(queryFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);
        return null;

    }

    /**
     * Inserts a deck into the database
     * @param deck
     */
    public static void insertDeck(Deck deck) {

        Connection conn = DatabaseHandler.getConnection();

        try {
            String insertSQL = "INSERT INTO studyhawk.Decks (title, description, favorite) VALUES(?,?,?)";
            PreparedStatement statement = conn.prepareStatement(insertSQL);

            statement.setString(1, deck.getTitle());
            statement.setString(2, deck.getDescription());
            statement.setBoolean(3, deck.getFavorite());

            statement.executeUpdate();

            System.out.printf("[DATABASE] Inserted into Decks table: %s%n", deck);

        } catch (SQLException insertFailed) {
            System.out.println("[ERROR] TABLE INSERT FAILED");
            System.out.println(insertFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);

    }

    /**
     * Inserts a card into the database
     * @param card The card to insert
     * @param deck The deck to insert the card into
     */
    public static void insertCard(Card card, Deck deck) {

        card.setDeckID(deck.getDeckID());

        Connection conn = DatabaseHandler.getConnection();

        try {
            String insertSQL = "INSERT INTO studyhawk.Cards (deckID, term, definition, favorite) VALUES(?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(insertSQL);

            statement.setInt(1, card.getDeckID());
            statement.setString(2, card.getTerm());
            statement.setString(3, card.getDefinition());
            statement.setBoolean(4, card.getFavorite());

            statement.executeUpdate();

            System.out.printf("[DATABASE] Inserted into Cards table: %s%n", card);

        } catch (SQLException insertFailed) {
            System.out.println("[ERROR] TABLE INSERT FAILED");
            System.out.println(insertFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);

    }

    /**
     * Inserts a user into the database
     * @param user The user to insert
     */
    public static void insertUser(UserAccount user) {

        Connection conn = DatabaseHandler.getConnection();

        try {
            String insertSQL = "INSERT INTO studyhawk.Users (username, email, password) VALUES(?,?,?)";
            PreparedStatement statement = conn.prepareStatement(insertSQL);

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());

            statement.executeUpdate();

            System.out.printf("[DATABASE] Inserted into Users table: %s%n", user);

        } catch (SQLException insertFailed) {
            System.out.println("[ERROR] TABLE INSERT FAILED");
            System.out.println(insertFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);

    }

    /**
     * Remove a deck from the database
     * @param deck The deck to remove
     */
    public static boolean removeDeck(Deck deck) {

        Connection conn = DatabaseHandler.getConnection();

        try {
            DatabaseHandler.removeCardsByDeck(deck, conn); // Remove all existing cards from the deck

            String removeSQL = "DELETE FROM studyhawk.Decks WHERE deckID = (?)";
            PreparedStatement statement = conn.prepareStatement(removeSQL);

            statement.setInt(1, deck.getDeckID());

            int rowUpdated = statement.executeUpdate();

            if (rowUpdated > 0) {
                System.out.printf("[DATABASE] Removed from Decks table: %s%n", deck);
                DatabaseHandler.closeConnection(conn);
                return true;
            } else {
                System.out.println("[ERROR] COULD NOT DELETE DECK. DECK DOES NOT EXIST");
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DELETING DECK FAILED");
            System.out.println(queryFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);
        return false;

    }

    /**
     * Remove a card from the database
     * @param card The card to remove
     */
    public static boolean removeCard(Card card) {

        Connection conn = DatabaseHandler.getConnection();

        try {
            String removeSQL = "DELETE FROM studyhawk.Cards WHERE cardID = (?)";
            PreparedStatement statement = conn.prepareStatement(removeSQL);

            statement.setInt(1, card.getCardID());

            int rowUpdated = statement.executeUpdate();

            if (rowUpdated > 0) {
                System.out.printf("[DATABASE] Removed from Cards table: %s%n", card);
                DatabaseHandler.closeConnection(conn);
                return true;
            } else {
                System.out.println("[ERROR] COULD NOT DELETE CARD. CARD DOES NOT EXIST");
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DELETING CARD FAILED");
            System.out.println(queryFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);
        return false;

    }

    /**
     * Removes any cards within a given deck
     * @param deck The deck to remove cards from
     * @param connection The connection passed to the method. This indicates this
     * method should only be used within the DatabaseHandler
     */
    private static boolean removeCardsByDeck(Deck deck, Connection conn) {

        try {
            String removeSQL = "DELETE FROM studyhawk.Cards WHERE deckID = (?)";
            PreparedStatement statement = conn.prepareStatement(removeSQL);

            statement.setInt(1, deck.getDeckID());

            int rowUpdated = statement.executeUpdate();

            if (rowUpdated > 0) {
                System.out.printf("[DATABASE] Removed existing cards from deck: %s%n", deck);
                return true;
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DELETING CARDS FAILED");
            System.out.println(queryFailed.getMessage());
        }

        return false;

    }

    /**
     * Toggles favorite for a deck
     * @param deck The deck to toggle favorite for
     * @param connection The connection passed to the method. This indicates this
     * method should only be used within the DatabaseHandler
     */
    public static boolean toggleFavoriteDeck(Deck deck) {

        Connection conn = DatabaseHandler.getConnection();

        try {
            String removeSQL = "UPDATE studyhawk.Decks SET favorite = 1 - favorite WHERE deckID = (?)";
            PreparedStatement statement = conn.prepareStatement(removeSQL);

            statement.setInt(1, deck.getDeckID());

            int rowUpdated = statement.executeUpdate();

            if (rowUpdated > 0) {
                System.out.printf("[DATABASE] Toggle favorite for deck: %s%n", deck);
                return true;
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] TOGGLING FAVORITE FOR DECKS FAILED");
            System.out.println(queryFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);
        return false;

    }

}
