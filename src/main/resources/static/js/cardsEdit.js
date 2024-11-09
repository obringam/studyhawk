var loadedDeckJSON; // The loaded deck info JSON at any given time
var loadedCardsJSON; // The loaded cards JSON at any given time
var numOfCards = 1;

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

function buildCard(card, cardList, i) {
    const cardCon = document.createElement("div");
    cardCon.classList.add("edit-card-object");

    // Build deck element
    cardCon.id = "card-" + card["cardID"];

    const cardNum = document.createElement("label");
    cardNum.classList.add("card-num");
    cardNum.innerHTML = i;
    const br1 = document.createElement("br");
    const term = document.createElement("input");
    term.classList.add("card-input");
    term.value = card["term"];
    term.placeholder = "Term";
    const definition = document.createElement("input");
    definition.classList.add("card-input");
    definition.value = card["definition"];
    definition.placeholder = "Definition";

    cardList.appendChild(cardCon);
    cardCon.appendChild(cardNum);
    cardCon.appendChild(br1);
    cardCon.appendChild(term);
    cardCon.appendChild(definition);
}

function addCard() {
    var newCard = {
        term: "",
        definition: ""
    };

    loadedCardsJSON[loadedCardsJSON.length] = (newCard);
    displayCards();
}

function displayCards() {
    const cardList = document.getElementById("editor-container");

    while (cardList.firstChild) {
        cardList.removeChild(cardList.lastChild);
    }

    for (let i = 0; i < loadedCardsJSON.length; i++) {
        let card = loadedCardsJSON[i];

        buildCard(card, cardList, i + 1);
    }
}

// Display the current deck info on the screen
function displayDeckInfo() {
    const title = document.getElementById("deck-title");
    title.value = loadedDeckJSON["title"];

    const decription = document.getElementById("deck-description");
    decription.value = loadedDeckJSON["description"];
}

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
            displayCards(); // Call function to display the current card
        },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });
}
