package org.studyhawk.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // Creates decks
    @PostMapping("/cards/req/create/{deckID}")
    public ResponseEntity<Map<String, String>> createCards(@PathVariable("deckID") String deckID, @RequestBody List<Card> cards, Errors errors) {
        Map<String, String> response = new HashMap<>();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            response.put("message", "[ERROR] Bad request");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        int intDeckID = Integer.parseInt(deckID);

        try {
            DatabaseHandler.insertCards(cards, intDeckID);
        } catch (AccessDeniedException e) {
            response.put("message", "You do not have permission to create cards in deck with ID " + intDeckID + "!");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("message", "Cards for deck with ID " + intDeckID + " were created successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Deletes decks
    @PostMapping("/cards/req/delete")
    public ResponseEntity<Map<String, String>> deleteCards(@RequestBody List<Card> cards, Errors errors) {
        Map<String, String> response = new HashMap<>();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            response.put("message", "[ERROR] Bad request");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            DatabaseHandler.removeCards(cards);
        } catch (AccessDeniedException e) {
            response.put("message", "You do not have permission to delete those cards!");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("message", "Cards were deleted successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Edits decks
    @PostMapping("/cards/req/edit")
    public ResponseEntity<Map<String, String>> editCards(@RequestBody List<Card> cards, Errors errors) {
        Map<String, String> response = new HashMap<>();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            response.put("message", "[ERROR] Bad request");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            DatabaseHandler.updateCards(cards);
        } catch (AccessDeniedException e) {
            response.put("message", "You do not have permission to update the cards!");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("message", "The cards were updated successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
