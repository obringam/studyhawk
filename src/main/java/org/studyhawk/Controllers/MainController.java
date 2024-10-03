package org.studyhawk.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MainController {

	@GetMapping("/")
	public ModelAndView index() {
		ModelAndView landingPage = new ModelAndView();
		landingPage.setViewName("index");
		return landingPage;
	}

	@GetMapping("/decks/add")
	public ModelAndView deck() {
		ModelAndView landingPage = new ModelAndView();
		landingPage.setViewName("deck");
		return landingPage;
	}

}
