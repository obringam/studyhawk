package org.studyhawk.Components;

import java.util.ArrayList;

public class Deck {
    private String title;
    private String description;
    private boolean favorite;
    private ArrayList<Card> cards = new ArrayList<Card>();

    public Deck(String title, String description) {
        this.title = title;
        this.description = description;
        favorite = false;
    }

    /** 
     * @return boolean
     */
    public boolean favorite() {
        if(favorite == false) {
            favorite = true;
        } else {
            favorite = false;
        }
        return favorite;
    }

    
    /** 
     * @param newCard
     */
    public void addCard(Card newCard) {
        cards.add(newCard);
    }

    public void listDeck() {
        for (int i = 0; i < cards.size(); i++) {
            System.out.println(cards.get(i).toString() + "\n");
        }
    }

    public String getTitle() {
        return title;
    }
}
