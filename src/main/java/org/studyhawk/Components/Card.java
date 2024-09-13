package org.studyhawk.Components;

public class Card{
    private String term;
    private String definition;
    private String image;
    private boolean favorite;
    
    public Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public Card(String term, String definition, String image) {
        this.term = term;
        this.definition = definition;
        this.image = image;
    }

    public String toString() {
        return term + "\n" + definition;
    }
}
