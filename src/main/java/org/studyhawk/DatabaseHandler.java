package org.studyhawk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.server.ResponseStatusException;
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
     * Gets all decks from the database for the current user.
     */
    public static ArrayList<Deck> getDecksForCurrentUser() {

        ArrayList<Deck> decks = new ArrayList<>();

        Connection conn = DatabaseHandler.getConnection();

        UserAccount currentUser = DatabaseHandler.getUserFromSecurityContext(conn);

        // Get decks from database
        try {

            // Query deck data from database
            String querySQL = "SELECT * FROM studyhawk.Decks WHERE userID = (?)";
            PreparedStatement statement = conn.prepareStatement(querySQL);

            statement.setInt(1, currentUser.getUserID());

            ResultSet result = statement.executeQuery();

            // Place all deck data in ArrayList
            try {
                while (result.next()) {
                    Deck deck = new Deck(
                        result.getString("title"),
                        result.getString("description"),
                        result.getBoolean("favorite"),
                        result.getBoolean("isPublic"),
                        result.getInt("userID")
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

    /**
     * Gets all shared decks from the database for the current user.
     */
    public static ArrayList<Deck> getSharedDecksForCurrentUser() {

        ArrayList<Deck> decks = new ArrayList<>();

        Connection conn = DatabaseHandler.getConnection();

        UserAccount currentUser = DatabaseHandler.getUserFromSecurityContext(conn);
        ArrayList<DeckOwnership> deckOwnerships = DatabaseHandler.getDeckOwnershipsByUserID(currentUser.getUserID(), conn);

        ArrayList<Integer> deckIDs = new ArrayList<>();
        for (DeckOwnership deckOwnership : deckOwnerships) {
            deckIDs.add(deckOwnership.getDeckID());
        }

        if (deckIDs.isEmpty()) {
            DatabaseHandler.closeConnection(conn);
            return decks;
        }

        // Get decks from database
        try {

            // Query deck data from database
            String inputSQL = String.join(",", deckIDs.stream().map(id -> "?").toArray(String[]::new));
            String querySQL = "SELECT * FROM studyhawk.Decks WHERE deckID IN (" + inputSQL + ")";
            PreparedStatement statement = conn.prepareStatement(querySQL);

            for (int i = 0; i < deckIDs.size(); i++) {
                statement.setInt(i + 1, deckIDs.get(i));
            }

            ResultSet result = statement.executeQuery();

            // Place all deck data in ArrayList
            try {
                while (result.next()) {
                    Deck deck = new Deck(
                        result.getString("title"),
                        result.getString("description"),
                        result.getBoolean("favorite"),
                        result.getBoolean("isPublic"),
                        result.getInt("userID")
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
            queryFailed.printStackTrace();
        }

        DatabaseHandler.closeConnection(conn);
        return decks;

    }

    /**
     * Get the deck from the database with the provided ID
     * @param ID The ID of the deck
     * @return The deck from the database
     */
    public static Deck getDeckByID(int ID) {

        Connection conn = DatabaseHandler.getConnection();

        Deck deck = DatabaseHandler.getDeckByID(ID, conn);

        DatabaseHandler.closeConnection(conn);

        return deck;

    }

    /**
     * Get the deck from the database with the provided ID
     * @param deckID The ID of the deck
     * @param connection The connection passed to the method. This indicates this
     * method should only be used within the DatabaseHandler
     * @return The deck from the database
     */
    public static Deck getDeckByID(int deckID, Connection conn) {

        // Get deck from database
        try {
            String querySQL = "SELECT * FROM studyhawk.Decks WHERE deckID = (?)";
            PreparedStatement statement = conn.prepareStatement(querySQL);

            statement.setInt(1, deckID);

            ResultSet result = statement.executeQuery();

            // Create deck object
            if (result.next()) {
                Deck deck = new Deck(
                    result.getString("title"),
                    result.getString("description"),
                    result.getBoolean("favorite"),
                    result.getBoolean("isPublic"),
                    result.getInt("userID")
                );
                deck.setDeckID(result.getInt("deckID"));

                int access = getAccessLevelToDeck(deck, conn);

                // If the user doesn't have read access, throw exception
                if (access < 0) {
                    throw new AccessDeniedException("The user is not authorized to view this deck!");
                }

                return deck;
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DECK QUERY FAILED");
            System.out.println(queryFailed.getMessage());
        }

        return null;

    }

    /**
     * Gets all cards from the database from a certain deck.
     * @param deckID The ID of the deck to get the cards from
     * @return ArrayList of Cards from the deck
     */
    public static ArrayList<Card> getCardsByDeckID(int deckID) {

        ArrayList<Card> cards = new ArrayList<>();

        Connection conn = DatabaseHandler.getConnection();

        Deck deck = DatabaseHandler.getDeckByID(deckID, conn); // Load the deck

        if (deck == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find deck");

        int access = getAccessLevelToDeck(deck, conn);

        // If the user doesn't have read access, throw exception
        if (access < 0) {
            throw new AccessDeniedException("The user is not authorized to view the cards in this deck");
        }

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
                        result.getBoolean("favorite"),
                        convertBlobToBase64(result.getBlob("image"))
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
     * Gets the deck ownership objects associated with a certain userID
     * @param userID The ID of the target user
     * @param conn The connection passed to the method. This indicates this
     * method should only be used within the DatabaseHandler
     * @return The user object
     */
    public static ArrayList<DeckOwnership> getDeckOwnershipsByUserID(int userID, Connection conn) {

        ArrayList<DeckOwnership> deckOwnerships = new ArrayList<>();

        // Get deck ownership objects from database
        try {

            String querySQL = "SELECT * FROM studyhawk.DeckOwnerships WHERE userID = (?)";
            PreparedStatement statement = conn.prepareStatement(querySQL);

            statement.setInt(1, userID);

            ResultSet result = statement.executeQuery();

            DeckOwnership deckOwnership = null;
            while (result.next()) {
                deckOwnership = new DeckOwnership(
                    result.getInt("deckID"),
                    result.getInt("userID"),
                    result.getInt("privelage")
                );
                deckOwnership.setDeckOwnershipID(result.getInt("deckOwnershipID"));
                deckOwnerships.add(deckOwnership);
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DECK OWNERSHIP QUERY FAILED");
            System.out.println(queryFailed.getMessage());
        }

        return deckOwnerships;

    }

    /**
     * Gets the deck ownership object associated with a certain userID and deckID
     * @param userID The ID of the target user
     * @param deckID The ID of the target deck
     * @param conn The connection passed to the method. This indicates this
     * method should only be used within the DatabaseHandler
     * @return The user object
     */
    public static DeckOwnership getDeckOwnership(int userID, int deckID, Connection conn) {

        DeckOwnership deckOwnership = null;

        // Get deck ownership objects from database
        try {

            String querySQL = "SELECT * FROM studyhawk.DeckOwnerships WHERE deckID = (?) AND userID = (?);";
            PreparedStatement statement = conn.prepareStatement(querySQL);

            statement.setInt(1, deckID);
            statement.setInt(2, userID);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                deckOwnership = new DeckOwnership(
                    result.getInt("deckID"),
                    result.getInt("userID"),
                    result.getInt("privelage")
                );
                deckOwnership.setDeckOwnershipID(result.getInt("deckOwnershipID"));
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DECK OWNERSHIP QUERY FAILED");
            System.out.println(queryFailed.getMessage());
        }

        return deckOwnership;

    }

    /**
     * Gets a user from the database with a certain username.
     * @param username The username of the user
     * @return The user object
     */
    public static UserAccount getUserByUsername(String username) {

        Connection conn = DatabaseHandler.getConnection();

        UserAccount user = DatabaseHandler.getUserByUsername(username, conn);

        DatabaseHandler.closeConnection(conn);

        return user;

    }

    /**
     * Gets a user from the database with a certain username.
     * @param username The username of the user
     * @param conn The connection passed to the method. This indicates this
     * method should only be used within the DatabaseHandler
     * @return The user object
     */
    public static UserAccount getUserByUsername(String username, Connection conn) {

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
            return user;


        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] USER QUERY FAILED");
            System.out.println(queryFailed.getMessage());
        }

        return null;

    }

    /**
     * Gets a user from the database attached to the security context
     * @return The user object from the database
     */
    public static UserAccount getUserFromSecurityContext() {
        User securityUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return DatabaseHandler.getUserByUsername(securityUser.getUsername());
    }

    /**
     * Gets a user from the database attached to the security context
     * @param connection The connection passed to the method. This indicates this
     * method should only be used within the DatabaseHandler
     * @return The user object from the database
     */
    public static UserAccount getUserFromSecurityContext(Connection connection) {
        User securityUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return DatabaseHandler.getUserByUsername(securityUser.getUsername(), connection);
    }

    /**
     * Update a deck in the database by its ID
     * @param deckID The ID of the deck
     * @param deck The new deck information
     */
    public static void updateDeck(Deck deck) {
        Connection conn = DatabaseHandler.getConnection();

        // Get current user ID
        int currentUserID = DatabaseHandler.getUserFromSecurityContext(conn).getUserID();

        try {
            String updateSQL = "UPDATE studyhawk.Decks SET title = ?, description = ? WHERE deckID = ? AND userID = ?";
            PreparedStatement statement = conn.prepareStatement(updateSQL);

            statement.setString(1, deck.getTitle());
            statement.setString(2, deck.getDescription());
            statement.setInt(3, deck.getDeckID());
            statement.setInt(4, currentUserID);

            statement.executeUpdate();

            System.out.printf("[DATABASE] Updated Deck in Decks table: %s%n", deck);

        } catch (SQLException updateFailed) {
            System.out.println("[ERROR] TABLE UPDATE FAILED");
            System.out.println(updateFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);

    }

    /**
     * Update cards in the database
     * @param cards The new card information
     */
    public static void updateCards(List<Card> cards) {
        if (cards.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot update 0 cards");

        Connection conn = DatabaseHandler.getConnection();

        getDeckByID(cards.get(0).getDeckID()); // Ensure access to the deck

        try {
            for (Card card : cards) {
                String updateSQL = "UPDATE studyhawk.Cards SET term = ?, definition = ?, image = ? WHERE cardID = ?";
                PreparedStatement statement = conn.prepareStatement(updateSQL);

                statement.setString(1, card.getTerm());
                statement.setString(2, card.getDefinition());
                if (card.getImage() == null) {
                    statement.setNull(3, java.sql.Types.BLOB);
                } else {
                    statement.setBytes(3, convertBase64ToBytes(card.getImage()));
                }
                statement.setInt(4, card.getCardID());

                statement.executeUpdate();

                System.out.printf("[DATABASE] Updated Card in Cards table: %s%n", card);
            }

        } catch (SQLException updateFailed) {
            System.out.println("[ERROR] TABLE UPDATE FAILED");
            System.out.println(updateFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);

    }

    /**
     * Update a card in the database
     * @param card The new card information
     */
    public static void updateCard(Card card) {
        Connection conn = DatabaseHandler.getConnection();

        getDeckByID(card.getDeckID()); // Ensure access to the deck

        try {
            String updateSQL = "UPDATE studyhawk.Cards SET term = ?, definition = ?, image = ? WHERE cardID = ?";
            PreparedStatement statement = conn.prepareStatement(updateSQL);

            statement.setString(1, card.getTerm());
            statement.setString(2, card.getDefinition());
            if (card.getImage() == null) {
                statement.setNull(3, java.sql.Types.BLOB);
            } else {
                statement.setBytes(3, convertBase64ToBytes(card.getImage()));
            }
            statement.setInt(4, card.getCardID());

            statement.executeUpdate();

            System.out.printf("[DATABASE] Updated Card in Cards table: %s%n", card);

        } catch (SQLException updateFailed) {
            System.out.println("[ERROR] TABLE UPDATE FAILED");
            System.out.println(updateFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);

    }

    /**
     * Inserts a deck into the database
     * @param deck
     */
    public static void insertDeck(Deck deck) {

        Connection conn = DatabaseHandler.getConnection();

        // Attach user identity to deck
        deck.setUserID(DatabaseHandler.getUserFromSecurityContext(conn).getUserID());

        try {
            String insertSQL = "INSERT INTO studyhawk.Decks (title, description, favorite, isPublic, userID) VALUES(?,?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(insertSQL);

            statement.setString(1, deck.getTitle());
            statement.setString(2, deck.getDescription());
            statement.setBoolean(3, deck.getFavorite());
            statement.setBoolean(4, deck.getIsPublic());
            statement.setInt(5, deck.getUserID());

            statement.executeUpdate();

            System.out.printf("[DATABASE] Inserted into Decks table: %s%n", deck);

        } catch (SQLException insertFailed) {
            System.out.println("[ERROR] TABLE INSERT FAILED");
            System.out.println(insertFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);

    }

    /**
     * Inserts a list of cards into the database
     * @param cards The cards to insert
     * @param deckID The ID of the deck to insert the cards into
     */
    public static void insertCards(List<Card> cards, int deckID) {

        for (Card card : cards) {
            card.setDeckID(deckID);
        }

        Connection conn = DatabaseHandler.getConnection();

        Deck deck = getDeckByID(deckID, conn); // Ensures access to the deck

        if (deck == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find deck");
        }

        try {
            for (Card card : cards) {
                String insertSQL = "INSERT INTO studyhawk.Cards (deckID, term, definition, favorite, image) VALUES(?,?,?,?,?)";
                PreparedStatement statement = conn.prepareStatement(insertSQL);

                statement.setInt(1, card.getDeckID());
                statement.setString(2, card.getTerm());
                statement.setString(3, card.getDefinition());
                statement.setBoolean(4, card.getFavorite());
                if (card.getImage() == null) {
                    statement.setNull(5, java.sql.Types.BLOB);
                } else {
                    statement.setBytes(5, convertBase64ToBytes(card.getImage()));
                }


                statement.executeUpdate();

                System.out.printf("[DATABASE] Inserted into Cards table: %s%n", card);
            }

        } catch (SQLException insertFailed) {
            System.out.println("[ERROR] TABLE INSERT FAILED");
            System.out.println(insertFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);

    }


    /**
     * Inserts a card into the database
     * @param card The card to insert
     * @param deckID The ID of the deck to insert the card into
     */
    public static void insertCard(Card card, int deckID) {

        card.setDeckID(deckID);

        Connection conn = DatabaseHandler.getConnection();

        Deck deck = getDeckByID(deckID, conn); // Ensures access to the deck

        if (deck == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find deck");
        }

        try {
            String insertSQL = "INSERT INTO studyhawk.Cards (deckID, term, definition, favorite, image) VALUES(?,?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(insertSQL);

            statement.setInt(1, card.getDeckID());
            statement.setString(2, card.getTerm());
            statement.setString(3, card.getDefinition());
            statement.setBoolean(4, card.getFavorite());
            if (card.getImage() == null) {
                statement.setNull(5, java.sql.Types.BLOB);
            } else {
                statement.setBytes(5, convertBase64ToBytes(card.getImage()));
            }

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
     * @throws Exception When the user already exists
     */
    public static void insertUser(UserAccount user) throws Exception {

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

            try {
                String querySQL = "SELECT * FROM studyhawk.Users WHERE username = (?)";
                PreparedStatement statement2 = conn.prepareStatement(querySQL);

                statement2.setString(1, user.getUsername());

                ResultSet result = statement2.executeQuery();

                if (result.next()) {
                    throw new Exception("That username is already taken");
                }

            } catch (SQLException queryFailed) {
                System.out.println("[ERROR] TABLE QUERY FAILED");
                throw new Exception("A table query failed!");
            }

            try {
                String querySQL = "SELECT * FROM studyhawk.Users WHERE email = (?)";
                PreparedStatement statement2 = conn.prepareStatement(querySQL);

                statement2.setString(1, user.getEmail());

                ResultSet result = statement2.executeQuery();

                if (result.next()) {
                    throw new Exception("That email is already taken");
                }

            } catch (SQLException queryFailed) {
                System.out.println("[ERROR] TABLE QUERY FAILED");
                throw new Exception("A table query failed!");
            }
        }

        DatabaseHandler.closeConnection(conn);

    }

    /**
     * Inserts a DeckOwnership relationship into the database
     * @param deckOwnership The deck ownership object
     * @param conn The connection passed to the method. This indicates this
     * method should only be used within the DatabaseHandler
     */
    public static void insertDeckOwnership(DeckOwnership deckOwnership, Connection conn) {

        try {
            String insertSQL = "INSERT INTO studyhawk.DeckOwnerships (deckID, userID, privelage) VALUES(?,?,?)";
            PreparedStatement statement = conn.prepareStatement(insertSQL);

            statement.setInt(1, deckOwnership.getDeckID());
            statement.setInt(2, deckOwnership.getUserID());
            statement.setInt(3, deckOwnership.getPrivelage());

            statement.executeUpdate();

            System.out.printf("[DATABASE] Inserted into DeckOwnerships table: %s%n", deckOwnership);

        } catch (SQLException insertFailed) {
            System.out.println("[ERROR] TABLE INSERT FAILED");
            System.out.println(insertFailed.getMessage());
        }

    }

    /**
     * Remove a deck from the database
     * @param deckID The ID of the deck to remove
     */
    public static boolean removeDeckByID(int deckID) throws AccessDeniedException {

        Connection conn = DatabaseHandler.getConnection();

        Deck deck = DatabaseHandler.getDeckByID(deckID, conn);

        int access = getAccessLevelToDeck(deck, conn);

        // If not the owner, throw exception
        if (access < 2) {
            throw new AccessDeniedException("The user is not authorized to delete this deck");
        }

        try {
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
     * Remove a deck sharing ownership from the database
     * @param deckID The ID of the deck ownership to remove
     */
    public static boolean removeSharedDeckByID(int deckID) throws AccessDeniedException {

        Connection conn = DatabaseHandler.getConnection();

        UserAccount currentUser = DatabaseHandler.getUserFromSecurityContext(conn);

        try {
            String querySQL = "DELETE FROM studyhawk.DeckOwnerships WHERE userID = (?) AND deckID = (?)";
            PreparedStatement statement = conn.prepareStatement(querySQL);

            statement.setInt(1, currentUser.getUserID());
            statement.setInt(2, deckID);

            int rowUpdated = statement.executeUpdate();

            if (rowUpdated > 0) {
                System.out.printf("[DATABASE] Removed from DeckOwnerships table for userID: %d and deckID: %d", currentUser.getUserID(), deckID);
                DatabaseHandler.closeConnection(conn);
                return true;
            } else {
                System.out.println("[ERROR] COULD NOT DELETE DECK OWNERSHIP. DECK OWNERSHIP DOES NOT EXIST");
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DELETING DECK OWNERSHIP FAILED");
            System.out.println(queryFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);
        return false;

    }

    /**
     * Remove cards from the database
     * @param cards The cards to remove
     */
    public static boolean removeCards(List<Card> cards) {

        if (cards.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot remove 0 cards");

        Connection conn = DatabaseHandler.getConnection();

        ArrayList<Integer> cardIDs = new ArrayList<>(); // Store all cardIDs to delete

        // Make sure all the cards have the same deckID
        int deckID = cards.get(0).getDeckID();
        for (Card card : cards) {
            if (card.getDeckID() != deckID)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot remove cards from multiple decks at once");

            cardIDs.add(card.getCardID()); // Add ID to list
        }
        getDeckByID(deckID); // Ensure access to the deck

        try {
            String inputSQL = String.join(",", cardIDs.stream().map(id -> "?").toArray(String[]::new));
            String removeSQL = "DELETE FROM studyhawk.Cards WHERE cardID IN (" + inputSQL + ")";
            PreparedStatement statement = conn.prepareStatement(removeSQL);

            for (int i = 0; i < cardIDs.size(); i++) {
                statement.setInt(i + 1, cardIDs.get(i));
            }

            int rowUpdated = statement.executeUpdate();

            if (rowUpdated > 0) {
                System.out.println("[DATABASE] Removed many cards from Cards table");
                DatabaseHandler.closeConnection(conn);
                return true;
            } else {
                System.out.println("[ERROR] COULD NOT DELETE CARDS. CARD DOES NOT EXIST");
            }

        } catch (SQLException queryFailed) {
            System.out.println("[ERROR] DELETING CARDS FAILED");
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
     * Attempt to share a deck with a user
     * @param deckID ID of the deck
     * @param username Username of the user to share it to
     * @throws Exception If user is not found
     */
    public static void shareDeckWithUser(int deckID, String username) throws Exception {

        Connection conn = DatabaseHandler.getConnection();

        // Retrieve the deck (checks access as well)
        Deck deck = DatabaseHandler.getDeckByID(deckID, conn);

        if (deck == null) {
            throw new Exception("The deck was not found!");
        }

        // Get the user for the sharing
        UserAccount user = DatabaseHandler.getUserByUsername(username);

        if (user == null) {
            throw new Exception("There is no user with the provided username!");
        }

        // Get current user
        UserAccount currentUser = DatabaseHandler.getUserFromSecurityContext(conn);

        // Ensure current user is not the user being shared to
        if (currentUser.getUserID() == user.getUserID()) {
            throw new Exception("You cannot share a deck to yourself!");
        }

        // See if a deckOwnership record exists
        DeckOwnership checkDeckOwnership = DatabaseHandler.getDeckOwnership(user.getUserID(), deckID, conn);

        if (checkDeckOwnership != null) {
            throw new Exception("The deck is alreay shared with that user!");
        }

        try {
            String insertSQL = "INSERT INTO studyhawk.DeckOwnerships (deckID, userID, privelage) VALUES (?,?,?)";
            PreparedStatement statement = conn.prepareStatement(insertSQL);

            statement.setInt(1, deckID);
            statement.setInt(2, user.getUserID());
            statement.setInt(3, 0);

            statement.executeUpdate();

            System.out.println("[DATABASE] Inserted into DeckOwnerships table");

        } catch (SQLException insertFailed) {
            System.out.println("[ERROR] TABLE INSERT FAILED");
            System.out.println(insertFailed.getMessage());
        }

        DatabaseHandler.closeConnection(conn);

    }

    /**
     * Toggles favorite for a deck
     * @param deck The deck to toggle favorite for
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

    /**
     * Return the level of access the current user has to the deck. (-1 - none, 0 - read, 1 - edit, 2 - owner)
     * @param deck The deck to check access for
     */
    public static int getAccessLevelToDeck(Deck deck) {
        Connection conn = DatabaseHandler.getConnection();

        int access = DatabaseHandler.getAccessLevelToDeck(deck, conn);

        DatabaseHandler.closeConnection(conn);

        return access;
    }

    /**
     * Return the level of access the current user has to the deck. (-1 - none, 0 - read, 1 - edit, 2 - owner)
     * @param deck The deck to check access for
     * @param conn The connection passed to the method. This indicates this
     * method should only be used within the DatabaseHandler
     */
    public static int getAccessLevelToDeck(Deck deck, Connection conn) {

        UserAccount currentUser = DatabaseHandler.getUserFromSecurityContext(conn);
        int ownerUserID = deck.getUserID();

        // If current user is the owner
        if (currentUser.getUserID() == ownerUserID)
            return 2;

        // Look for deck ownership for certain deck and user
        DeckOwnership deckOwnership = DatabaseHandler.getDeckOwnership(currentUser.getUserID(), deck.getDeckID(), conn);

        if (deckOwnership == null) {
            return -1;
        } else {
            return deckOwnership.getPrivelage();
        }

    }

    /**
     * Converts a blob to base64
     * @param blob Blob object
     * @return Base64 string
     */
    public static String convertBlobToBase64(Blob blob) {
        if (blob == null)
            return null;

        try {
            // Get the binary data as a byte array
            byte[] blobBytes = blob.getBytes(1, (int) blob.length());

            // Encode the byte array to a Base64 string
            String base64String = Base64.getEncoder().encodeToString(blobBytes);

            return base64String;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a base64 string to a byte array for blob insertion
     * @param base64String Base64 string
     * @return Byte array
     */
    public static byte[] convertBase64ToBytes(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

}
