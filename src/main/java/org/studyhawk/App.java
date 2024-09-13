package org.studyhawk;

import javafx.application.Application;
import javafx.stage.Stage;
import org.studyhawk.Components.Card;
import org.studyhawk.Components.Deck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class App extends Application {

    public void start(Stage stage) throws IOException {

//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/MainView.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 1520, 720);
//        stage.setTitle("StudyHawk");
//        stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("Images/icon.png"))));
//        stage.setScene(scene);

//        stage.show();

        ArrayList<Deck> deckList = new ArrayList<Deck>();

        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            System.out.println("1. Create Deck");
            System.out.println("2. Create Card");
            System.out.println("3. List Deck");
            System.out.println("4. Exit");
            input = scanner.nextLine();

            if (input.equals("1")) {
                System.out.println("Enter the name of the deck: ");
                String title = scanner.nextLine();
                System.out.println("Enter a deck description: ");
                String description = scanner.nextLine();
                deckList.add(new Deck(title, description));
            }
            if (input.equals("3")) {
                listDecks(deckList);
            }
        } while(!input.equals(4));
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
