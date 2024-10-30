package org.studyhawk.Controllers;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.studyhawk.Components.Card;
import org.studyhawk.DatabaseHandler;

@RestController
public class CardController {

    // Page to study cards
    @GetMapping("/cards/{deckID}")
	public ModelAndView getCardsPage(@PathVariable("deckID") String deckID) {
		return new ModelAndView("cards");
    }

    // Page to edit cards
    @GetMapping("/cards/edit/{deckID}")
	public ModelAndView getCardsEditPage(@PathVariable("deckID") String deckID) {
		return new ModelAndView("cardsEdit");
    }

    // Retrieves all cards from a deck
    @GetMapping("/cards/get/{deckID}")
    public ResponseEntity<?> getCards(@PathVariable("deckID") String deckID) {
        ArrayList<Card> cards;
        try {
            cards = DatabaseHandler.getCardsByDeckID(Integer.parseInt(deckID));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid deckID");
        }
        return ResponseEntity.ok(cards);
    }

}
