package org.studyhawk.Components;

public class DeckOwnership {

    private int deckOwnershipID;
    private int deckID;
    private int userID;
    private int privelage; // 0 for reading, 1 for editing

    public DeckOwnership(int deckID, int userID, int privelage) {
        this.deckID = deckID;
        this.userID = userID;
        this.privelage = privelage;
    }

    public int getDeckOwnershipID() {
        return this.deckOwnershipID;
    }

    public void setDeckOwnershipID(int deckOwnershipID) {
        this.deckOwnershipID = deckOwnershipID;
    }

    public int getDeckID() {
        return this.deckID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }

    public int getUserID() {
        return this.userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getPrivelage() {
        return this.privelage;
    }

    public void setPrivelage(int privelage) {
        this.privelage = privelage;
    }

}
