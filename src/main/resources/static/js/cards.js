var loadedDeckJSON; // The loaded deck info JSON at any given time
var loadedCardsJSON; // The loaded cards JSON at any given time
var cardIndex = 0; // Index of card currently being displayed
var flipped = false; // True if card is flipped and definition is being displayed

// Runs on startup of page
function loadPage() {
    loadDeckInfo();
    loadCards();
}

// Retrieves the deckID from the URL
function getDeckID() {
    const url = window.location.pathname;
    const id = url.substring(url.lastIndexOf('/') + 1);
    return id;
}

// Moves back a card
function prevCard() {
    if (cardIndex > 0) {
        cardIndex--;
        flipped = false;
        displayCard();
    }
}

// Moves forward a card
function nextCard() {
    if (cardIndex < loadedCardsJSON.length - 1) {
        cardIndex++;
        flipped = false;
        displayCard();
    }
}

// Flips the card between term and definition
function flipCard() {
    flipped = !flipped;
    displayCard();
}

// Display the current deck info on the screen
function displayDeckInfo() {
    const title = document.getElementById("deck-title");
    title.textContent = loadedDeckJSON["title"];
}

// Display the current card to the screen
function displayCard() {
    const cardContainer = document.getElementById("card-container");
    const cardButtonContainer = document.getElementById("card-button-container");

    // Clear cards from card container
    while (cardContainer.firstChild) {
        cardContainer.removeChild(cardContainer.lastChild);
    }

    const cardDiv = document.createElement("div");
    cardDiv.classList.add("card-style");

    // If there are no cards in the deck
    if (loadedCardsJSON.length == 0) {

        // Display message
        const message = document.createTextNode("There are no cards in this deck...");
        cardDiv.appendChild(message);
        cardDiv.classList.add("definition");

        // Remove buttons
        cardButtonContainer.style.display = "none";

    } else {

        // Create card object and display
        let card = loadedCardsJSON[cardIndex];

        cardDiv.id = "card-" + card["cardID"];
        cardDiv.onclick = function () {flipCard()};

        if (flipped) {
            // Definition is shown
            const definition = document.createTextNode(card["definition"]);
            cardDiv.appendChild(definition);
            cardDiv.classList.add("definition");
        } else {
            // Term is shown
            const term = document.createTextNode(card["term"]);
            cardDiv.appendChild(term);
            cardDiv.classList.add("term");
        }

        // Update card position label
        const cardPos = document.getElementById("card-position");
        cardPos.textContent = (cardIndex + 1) + " / " + loadedCardsJSON.length;

        // Show buttons
        cardButtonContainer.style.display = "flex";

    }

    // Add card to container
    cardContainer.appendChild(cardDiv);
}

// Sends an ajax request to get info about the deck
function loadDeckInfo() {
    const id = getDeckID();

    var requestURL = "/decks/get/" + id;
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: requestURL,
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("SUCCESS : ", data);
            loadedDeckJSON = data; // Assign cards data to global variable
            displayDeckInfo();
        },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });
}

// Sends an ajax request to get cards from deck
function loadCards() {
    const id = getDeckID();

    var requestURL = "/cards/get/" + id;
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: requestURL,
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("SUCCESS : ", data);
            loadedCardsJSON = data; // Assign cards data to global variable
            cardIndex = 0; // Resets card index
            displayCard(); // Call function to display the current card
        },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });
}