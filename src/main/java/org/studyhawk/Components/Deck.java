package org.studyhawk.Components;

import java.util.Objects;

public class Deck {
    private int deckID;
    private String title;
    private String description;
    private boolean favorite;

    public Deck() {}

    public Deck(String title, String description) {
        this(title, description, false);
    }

    public Deck(String title, String description, boolean favorite) {
        this.title = title;
        this.description = description;
        this.favorite = favorite;
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

    @Override
    public String toString() {
        if (deckID == 0)
            return String.format("[DECK] Title: %s, Description %s, Favorite: %b", this.title, this.description, this.favorite);
        else
            return String.format("[DECK] ID: %d, Title: %s, Description %s, Favorite: %b", this.deckID, this.title, this.description, this.favorite);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Deck))
            return false;
        Deck deck = (Deck) o;
        return Objects.equals(this.deckID, deck.deckID) && Objects.equals(this.title, deck.title)
            && Objects.equals(this.description, deck.description) && Objects.equals(this.favorite, deck.favorite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.deckID, this.title, this.description, this.favorite);
    }

    /**
     * @return boolean
     */
    public boolean favorite() {
        favorite = !favorite;
        return favorite;
    }

    /**
     * @param newCard
     */
    public void addCard(Card newCard) {}

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