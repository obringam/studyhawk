package org.studyhawk.Components;

public class Card{
    private int cardID;
    private int deckID;
    private String term;
    private String definition;
    private boolean favorite;
    private String image;

    public Card() {}

    public Card(String term, String definition, boolean favorite, String image) {
        this(0, term, definition, favorite, image);
    }

    public Card(int deckID, String term, String definition, boolean favorite, String image) {
        this.deckID = deckID;
        this.term = term;
        this.definition = definition;
        this.favorite = favorite;
        this.image = image;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCardID() {
        return this.cardID;
    }

    public int getDeckID() {
        return this.deckID;
    }

    public String getTerm() {
        return this.term;
    }

    public String getDefinition() {
        return this.definition;
    }

    public boolean getFavorite() {
        return this.favorite;
    }

    public String getImage() {
        return this.image;
    }

    @Override
    public String toString() {
        if (cardID == 0)
            return String.format("[CARD] DeckID: %d, Term: %s, Definition %s, Favorite: %b", this.deckID, this.term, this.definition, this.favorite);
        else
            return String.format("[CARD] ID: %d, DeckID: %d, Term: %s, Definition %s, Favorite: %b", this.cardID, this.deckID, this.term, this.definition, this.favorite);
    }
}
