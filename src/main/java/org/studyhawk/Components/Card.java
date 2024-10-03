package org.studyhawk.Components;

public class Card{
    private int cardID;
    private String term;
    private String definition;
    private boolean favorite;

    public Card() {}

    public Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public Card(String term, String definition, boolean favorite) {
        this.term = term;
        this.definition = definition;
        this.favorite = favorite;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
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

    public int getCardID() {
        return this.cardID;
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

    @Override
    public String toString() {
        if (cardID == 0)
            return String.format("[CARD] Term: %s, Definition %s, Favorite: %b", this.term, this.definition, this.favorite);
        else
            return String.format("[CARD] ID: %d, Term: %s, Definition %s, Favorite: %b", this.cardID, this.term, this.definition, this.favorite);
    }
}
