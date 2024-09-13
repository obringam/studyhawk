package org.studyhawk.Components;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Deck> deckList = new ArrayList<Deck>();

        Deck dogs = new Deck("Dogs", "Dogs and their Behaviors.");
        deckList.add(dogs);
        Card blackLab = new Card("Black Lab", "Black Labrador Retrievers are a British breed of dog that originated in Newfoundland and the United Kingdom. They are known for being intelligent, outgoing, and even-tempered.");
        Card newfoundland = new Card("Newfoundland", "The Newfoundland is a large breed of working dog. They can be black, grey, brown, or black and white. However, in the Dominion of Newfoundland, before it became part of the confederation of Canada, only black and Landseer coloured dogs were considered to be proper members of the breed.");
        dogs.addCard(blackLab);
        dogs.addCard(newfoundland);
        dogs.listDeck();

        listDecks(deckList);
    }

    private static void listDecks(ArrayList<Deck> deckList) {
        for(int i = 0; i < deckList.size(); i++) {
            System.out.println(deckList.get(i).getTitle());
        }
    }
}