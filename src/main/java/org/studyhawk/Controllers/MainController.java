package org.studyhawk.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MainController {

	@GetMapping("/")
	public ModelAndView getHomePage() {
		return new ModelAndView("redirect:/decks");
	}

	@GetMapping("/signup")
	public ModelAndView signup() {
		return new ModelAndView("signup");
	}

	@GetMapping("/login")
	public ModelAndView login(@RequestParam(name = "logout", required = false) String logout) {
		ModelAndView model = new ModelAndView("login");
		model.addObject("logout", logout != null);
		return model;
	}

}
