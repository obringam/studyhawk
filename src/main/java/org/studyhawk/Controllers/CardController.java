package org.studyhawk.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.ModelAndView;

@RestController
public class CardController {

    // Page to view decks
    @GetMapping("/cards/{deckID}")
	public ModelAndView getCardsPage(@PathVariable("deckID") String deckID) {
		return new ModelAndView("cards");
    }

}
