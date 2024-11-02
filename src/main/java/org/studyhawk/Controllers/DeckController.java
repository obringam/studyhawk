package org.studyhawk.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.ModelAndView;
import org.studyhawk.DatabaseHandler;
import org.studyhawk.Components.Deck;

import java.util.ArrayList;

@RestController
public class DeckController {

    // Page to view decks
    @GetMapping("/decks")
	public ModelAndView getDecksPage() {
		return new ModelAndView("decks");
	}

    // Retrieves all decks
    @GetMapping("/decks/get")
    public ResponseEntity<?> getDecks() {
        ArrayList<Deck> decks = DatabaseHandler.getDecks();
        return ResponseEntity.ok(decks);
    }

    // Retrieves a deck by its ID
    @GetMapping("/decks/get/{deckID}")
    public ResponseEntity<?> getCards(@PathVariable("deckID") String deckID) {
        Deck deck;
        try {
            deck = DatabaseHandler.getDeckByID(Integer.parseInt(deckID));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid deckID");
        }
        return ResponseEntity.ok(deck);
    }

    // Adds a deck
    @PostMapping("/decks/add")
    public ResponseEntity<?> addDeck(@RequestBody Deck deck, Errors errors) {
        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        DatabaseHandler.insertDeck(deck);
        return ResponseEntity.ok(deck);
    }

    // Removes a deck
    @PostMapping("/decks/remove")
    public ResponseEntity<?> removeDeck(@RequestBody Deck deck, Errors errors) {
        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        DatabaseHandler.removeDeck(deck);
        return ResponseEntity.ok(deck);
    }

    // Favorites a deck
    @PostMapping("/decks/favorite")
    public ResponseEntity<?> favoriteDeck(@RequestBody Deck deck, Errors errors) {
        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        DatabaseHandler.toggleFavoriteDeck(deck);
        return ResponseEntity.ok(deck);
    }

}
