package org.studyhawk.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MainController {

	@GetMapping("/")
	public ModelAndView getHomePage() {
		return new ModelAndView("redirect:/decks");
	}

}
