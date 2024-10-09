package org.studyhawk.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestParam;
import org.studyhawk.DatabaseHandler;
import org.studyhawk.Components.Deck;

import java.util.ArrayList;

@RestController
public class DeckController {

    @GetMapping("/decks")
	public ModelAndView getDecksPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("decks");

        // Add all decks from database
        ArrayList<Deck> decks = DatabaseHandler.getDecks();
        model.addObject("decks", decks);
        model.addObject("deck1", decks.get(0));

		return model;
	}

    @GetMapping("/decks/add")
    public ModelAndView addDeck(@RequestParam(required = true, name = "title") String title, @RequestParam(required = true, name = "description") String description) {
        Deck deck = new Deck(title, description);
        DatabaseHandler.insertDeck(deck);
        return new ModelAndView("redirect:/decks");
    }

    @GetMapping("/decks/remove")
    public ModelAndView removeDeck(@RequestParam(required = true, name = "deck") String deckID) {
        try {
            int ID = Integer.parseInt(deckID);
            Deck targetDeck = DatabaseHandler.getDeckByID(ID);
            DatabaseHandler.removeDeck(targetDeck);
        } catch (NumberFormatException e) {
            System.out.println("[CONTROLLER] INVALID DECK ID");
            return new ModelAndView("redirect:/decks");
        }
        return new ModelAndView("redirect:/decks");
    }

}
