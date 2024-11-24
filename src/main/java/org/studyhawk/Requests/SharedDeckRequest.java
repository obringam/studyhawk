package org.studyhawk.Requests;

/**
 * Class for a request to share a deck
 */
public class SharedDeckRequest {
    private String deckID;
    private String title;
    private String username;

    public String getDeckID() {
        return deckID;
    }

    public void setDeckID(String deckID) {
        this.deckID = deckID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
