package org.studyhawk.Controllers;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.studyhawk.Components.Card;
import org.studyhawk.Components.Deck;
import org.studyhawk.DatabaseHandler;

@RestController
public class CardController {

    // Page to study cards
    @GetMapping("/cards/{deckID}")
	public ModelAndView getCardsPage(@PathVariable("deckID") String deckID) {
        int intDeckID = Integer.parseInt(deckID);
        Deck deck = DatabaseHandler.getDeckByID(intDeckID);
        if (deck == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find deck");
		return new ModelAndView("cards");
    }

    // Page to edit cards
    @GetMapping("/cards/edit/{deckID}")
	public ModelAndView getCardsEditPage(@PathVariable("deckID") String deckID) {
        int intDeckID = Integer.parseInt(deckID); // Causes 500 error if not an int
        Deck deck = DatabaseHandler.getDeckByID(intDeckID); // Causes 403 forbidden error if not authorized

        // Throw 404 not found if deck was not found
        if (deck == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find deck");

        // Throw 403 forbidden error if not authorized to edit
        int access = DatabaseHandler.getAccessLevelToDeck(deck);
        if (access < 1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient edit access to deck");

        return new ModelAndView("cardsEdit");
    }

    // Retrieves all cards from a deck
    @GetMapping("/cards/get/{deckID}")
    public ResponseEntity<?> getCards(@PathVariable("deckID") String deckID) {
        int intDeckID = Integer.parseInt(deckID);
        ArrayList<Card> cards = DatabaseHandler.getCardsByDeckID(intDeckID);
        return ResponseEntity.ok(cards);
    }

}
