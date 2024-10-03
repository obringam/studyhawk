package org.studyhawk.Components;


import java.util.ArrayList;


import java.util.Random;


public class Deck {
    private String title;
    private String description;
    private boolean favorite;
    private ArrayList<Card> cards;


    public Deck(String title, String description) {
        this.title = title;
        this.description = description;
        favorite = false;
        cards = new ArrayList<Card>();


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

    public void shuffle() {
        Random random = new Random();
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }


}