package org.studyhawk.Components;

public class Deck {
    private int deckID;
    private String title;
    private String description;
    private boolean favorite;
    private boolean isPublic; // True if the deck is a public deck
    private int userID; // The owner of the deck

    public Deck(String title, String description, boolean favorite, boolean isPublic, int userID) {
        this.title = title;
        this.description = description;
        this.favorite = favorite;
        this.isPublic = isPublic;
        this.userID = userID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getDeckID() {
        return this.deckID;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getFavorite() {
        return this.favorite;
    }

    public boolean getIsPublic() {
        return this.isPublic;
    }

    public int getUserID() {
        return this.userID;
    }

    @Override
    public String toString() {
        if (deckID == 0)
            return String.format("[DECK] Title: %s, Description %s, Favorite: %b, Public: %b, Owner: %d", this.title, this.description, this.favorite, this.isPublic, this.userID);
        else
            return String.format("[DECK] ID: %d, Title: %s, Description %s, Favorite: %b, Public: %b, Owner: %d", this.deckID, this.title, this.description, this.favorite, this.isPublic, this.userID);
    }

    // public void shuffle() {
    //     Random random = new Random();
    //     for (int i = cards.size() - 1; i > 0; i--) {
    //         int j = random.nextInt(i + 1);

    //         Card temp = cards.get(i);
    //         cards.set(i, cards.get(j));
    //         cards.set(j, temp);
    //     }
    // }


}
