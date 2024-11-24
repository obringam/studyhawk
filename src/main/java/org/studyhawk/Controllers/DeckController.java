package org.studyhawk.Controllers;

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
import org.studyhawk.DatabaseHandler;
import org.studyhawk.Components.Deck;
import org.studyhawk.Requests.SharedDeckRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DeckController {

    // Page to view decks
    @GetMapping("/decks")
	public ModelAndView getDecksPage() {
		return new ModelAndView("decks");
	}

    // Retrieves all owned decks
    @GetMapping("/decks/get")
    public ResponseEntity<?> getDecks() {
        ArrayList<Deck> decks = DatabaseHandler.getDecksForCurrentUser();
        return ResponseEntity.ok(decks);
    }

    // Retrieves all shared decks
    @GetMapping("/decks/get/shared")
    public ResponseEntity<?> getSharedDecks() {
        ArrayList<Deck> decks = DatabaseHandler.getSharedDecksForCurrentUser();
        return ResponseEntity.ok(decks);
    }

    // Retrieves a deck by its ID
    @GetMapping("/decks/get/{deckID}")
    public ResponseEntity<?> getCards(@PathVariable("deckID") String deckID) {
        int intDeckID = Integer.parseInt(deckID);
        Deck deck = DatabaseHandler.getDeckByID(intDeckID);
        if (deck == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find deck");
        return ResponseEntity.ok(deck);
    }

    // Adds a deck
    @PostMapping("/decks/add")
    public ResponseEntity<Map<String, String>> addDeck(@RequestBody Deck deck, Errors errors) {
        Map<String, String> response = new HashMap<>();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            response.put("message", "[ERROR] Bad request");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        DatabaseHandler.insertDeck(deck);

        response.put("message", deck.getTitle() + " was added successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Removes a deck
    @PostMapping("/decks/remove")
    public ResponseEntity<Map<String, String>> removeDeck(@RequestBody Deck deck, Errors errors) {
        Map<String, String> response = new HashMap<>();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            response.put("message", "[ERROR] Bad request");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            DatabaseHandler.removeDeckByID(deck.getDeckID());
        } catch (AccessDeniedException e) {
            response.put("message", "You do not have permission to delete " + deck.getTitle() + "!");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("message", deck.getTitle() + " was deleted successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Removes a deck from being shared with a user
    @PostMapping("/decks/shared/remove")
    public ResponseEntity<Map<String, String>> removeSharedDeck(@RequestBody Deck deck, Errors errors) {
        Map<String, String> response = new HashMap<>();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            response.put("message", "[ERROR] Bad request");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            DatabaseHandler.removeSharedDeckByID(deck.getDeckID());
        } catch (AccessDeniedException e) {
            response.put("message", "You do not have permission to remove " + deck.getTitle() + " from shared decks!");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("message", deck.getTitle() + " was removed from shared decks successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Removes a deck from being shared with a user
    @PostMapping("/decks/shared/share")
    public ResponseEntity<Map<String, String>> shareDeck(@RequestBody SharedDeckRequest request, Errors errors) {
        Map<String, String> response = new HashMap<>();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            response.put("message", "[ERROR] Bad request");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        int deckID = Integer.parseInt(request.getDeckID());

        // Attempt to share deck with user
        try {
            DatabaseHandler.shareDeckWithUser(deckID, request.getUsername());
        } catch (AccessDeniedException e) {
            response.put("message", "You do not have permission to share " + request.getTitle() + " this deck!");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("message", request.getTitle() + " was removed from shared decks successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Update a deck
    @PostMapping("/decks/update")
    public ResponseEntity<Map<String, String>> updateDeck(@RequestBody Deck deck, Errors errors) {
        Map<String, String> response = new HashMap<>();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            response.put("message", "[ERROR] Bad request");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            DatabaseHandler.updateDeck(deck);
        } catch (AccessDeniedException e) {
            response.put("message", "You do not have permission to update " + deck.getTitle() + "!");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("message", deck.getTitle() + " was updated successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
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
