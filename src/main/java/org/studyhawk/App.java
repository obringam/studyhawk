package org.studyhawk;

import javafx.application.Application;
import javafx.stage.Stage;
import org.studyhawk.Components.Card;
import org.studyhawk.Components.Deck;

import java.io.IOException;
import java.util.ArrayList;

public class App extends Application {

    public void start(Stage stage) throws IOException {

//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/MainView.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 1520, 720);
//        stage.setTitle("StudyHawk");
//        stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("Images/icon.png"))));
//        stage.setScene(scene);

//        stage.show();

        ArrayList<Deck> deckList = new ArrayList<Deck>();

        Deck dogs = new Deck("Dogs", "Dogs and their Behaviors.");
        deckList.add(dogs);
        Card blackLab = new Card("Black Lab", "Black Labrador Retrievers are a British breed of dog that originated in Newfoundland and the United Kingdom. They are known for being intelligent, outgoing, and even-tempered.");
        Card newfoundland = new Card("Newfoundland", "The Newfoundland is a large breed of working dog. They can be black, grey, brown, or black and white. However, in the Dominion of Newfoundland, before it became part of the confederation of Canada, only black and Landseer coloured dogs were considered to be proper members of the breed.");
        dogs.addCard(blackLab);
        dogs.addCard(newfoundland);
        dogs.listDeck();

        listDecks(deckList);

    }

    private static void listDecks(ArrayList<Deck> deckList) {
        for(int i = 0; i < deckList.size(); i++) {
            System.out.println(deckList.get(i).getTitle());
        }
    }

    public static void main(String[] args) {
        launch();
    }

}
