package org.studyhawk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.studyhawk.Components.Deck;

import java.util.ArrayList;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		ArrayList<Deck> decks = DatabaseHandler.getDecks();

		for (Deck deck : decks) {
			System.out.println(deck);
		}

		SpringApplication.run(Application.class, args);

    }

}
