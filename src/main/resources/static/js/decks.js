$(document).ready(function () {

    // Override default submit behavior for the add-form form
    $("#add-form").submit(function (event) {
        event.preventDefault();
        if (this.checkValidity()) {
            // Hide the form if validation passes
            hide_form();
            request_add_deck();
        } else {
            // If validation fails, let the browser show the default validation messages
            this.reportValidity();
        }
    });

    // Override default submit behavior for the search-form form
    $("#search-form").submit(function (event) {
        event.preventDefault();
        // Call search function with search text as parameter
        search($(this).find('#search-input').val().toLowerCase());
    });

});

var loadedDecksJSON; // The loaded decks JSON at any given time
var searchText; // The current search text entered and submitted at any given time

// Runs on startup of page
function loadPage() {
    loadDecks();
}

// Updates the search texts and redisplays the decks
function search(text) {
    // Update global var with search text from form (change to all lowercase)
    searchText = text;
    // Call display decks to update the displayed decks
    displayDecks();
}

// Navigate to cards page for selected deck
function view_deck(id) {
    var extractedID = id.substring(5); // Remove prefix from deckID
    var newURL = window.location.origin + "/cards/" + extractedID;
    window.location.href = newURL; // Navigate to new URL
}

// Displays the decks. The decks are filtered if a search text is entered.
function displayDecks() {
    const deckList = document.getElementById("deck-list");

    // Clear decks from deck list
    while (deckList.firstChild) {
        deckList.removeChild(deckList.lastChild);
    }

    // For each deck returned, create an element and add to deck list
    var deckCount = 0; // Count the number of decks created
    for(let i = 0; i < loadedDecksJSON.length; i++) {
        let deck = loadedDecksJSON[i];

        // If search text is not empty, check if deck title or description
        // contains the text. If it doesn't, skip this deck.
        if (typeof searchText !== 'undefined') { // Undefined if no search has been entered yet
            if (searchText.length > 0) { // 0 if the search text is nothing
                // If search text isn't in the title or description
                if (!deck["title"].toLowerCase().includes(searchText)
                    && !deck["description"].toLowerCase().includes(searchText)) {
                    continue;
                }
            }
        }

        // Build deck element
        const deckDiv = document.createElement("div");
        deckDiv.classList.add("decks");
        deckDiv.id = "deck-" + deck["deckID"];

        const titleDiv = document.createElement("div");
        titleDiv.classList.add("title");
        const titleHeader = document.createElement("h3");
        const title = document.createTextNode(deck["title"]);
        const hr = document.createElement("hr");

        const descriptionDiv = document.createElement("div");
        descriptionDiv.classList.add("description");
        const descriptionPara = document.createElement("p");
        const description = document.createTextNode(deck["description"]);
        const studyButton = document.createElement("Button");
        studyButton.classList.add("studybtn");
        studyButton.classList.add("deckbtn");
        studyButton.onclick = function() {view_deck(deckDiv.id)};
        const viewText = document.createTextNode("Study");
        const deleteButton = document.createElement("Button");
        deleteButton.classList.add("deletebtn");
        deleteButton.classList.add("deckbtn");
        deleteButton.onclick = function() {request_delete_deck(deleteButton, deckDiv.id)};
        const deleteText = document.createTextNode("Delete");
        const favoriteSpan = document.createElement("Span");
        favoriteSpan.classList.add("fa");
        favoriteSpan.classList.add("fa-star");
        favoriteSpan.classList.add("favoritespan")
        // favoriteSpan.classList.add("deckbtn");
        if (deck["favorite"]) {
            favoriteSpan.classList.add("checked");
        }
        favoriteSpan.onclick = function() {toggle_favorite(favoriteSpan, deckDiv.id)};


        // Combine elements
        studyButton.appendChild(viewText);
        deleteButton.appendChild(deleteText);
        titleHeader.appendChild(title);
        titleDiv.appendChild(titleHeader);
        descriptionPara.appendChild(description);
        descriptionDiv.appendChild(descriptionPara);
        descriptionDiv.appendChild(studyButton);
        descriptionDiv.appendChild(deleteButton);
        descriptionDiv.appendChild(favoriteSpan);
        deckDiv.appendChild(titleDiv);
        deckDiv.appendChild(hr);
        deckDiv.appendChild(descriptionDiv);

        // Add deck to deck list
        deckList.appendChild(deckDiv);
        deckCount++;
    }

    if (deckCount == 0) {
        const noDecksDiv = document.createElement("div");
        noDecksDiv.classList.add("no-decks-div");
        const noDecksPara = document.createElement("p");

        var noDecksText = document.createTextNode("You have no decks! Press 'Add Deck' to create one!");
        if (typeof searchText !== 'undefined') { // Undefined if no search has been entered yet
            if (searchText.length > 0) { // 0 if the search text is nothing
                noDecksText = document.createTextNode("We couldn't find any decks matching your search!");
            }
        }

        noDecksPara.appendChild(noDecksText);
        noDecksDiv.appendChild(noDecksPara);
        deckList.appendChild(noDecksDiv);
    }
}

// Sends an ajax request to get decks
function loadDecks() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/decks/get",
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("SUCCESS : ", data);
            loadedDecksJSON = data; // Assign deck data to global variable
            displayDecks(); // Call function to display the decks
        },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });
}

// Sends an ajax request to add a deck
function request_add_deck() {
    var requestData = {}
    requestData["title"] = $("#title").val();
    requestData["description"] = $("#description").val();

    $("#submitbtn").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/decks/add",
        data: JSON.stringify(requestData),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("SUCCESS : ", data);
            loadDecks();
            $("#submitbtn").prop("disabled", false);
        },
        error: function (e) {
            console.log("ERROR : ", e);
            $("#submitbtn").prop("disabled", false);
        }
    });
}

// Sends an ajax request to delete a deck
function request_delete_deck(element, id) {

    var requestData = {}
    requestData["deckID"] = id.substring(5); // Remove prefix from deckID

    element.disabled = true;

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/decks/remove",
        data: JSON.stringify(requestData),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("SUCCESS : ", data);
            loadDecks();
        },
        error: function (e) {
            console.log("ERROR : ", e);
            element.disabled = false;
        }
    });
}

// Sends an ajax request to toggle favorite on a deck
function request_favorite_deck(id) {
    var requestData = {}
    requestData["deckID"] = id.substring(5); // Remove prefix from deckID

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/decks/favorite",
        data: JSON.stringify(requestData),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("SUCCESS : ", data);
            loadDecks();
        },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });
}

// Toggle favorite on deck
function toggle_favorite(element, id) {

    if (element.classList.contains("checked")) {
        element.classList.remove("checked");
    } else {
        element.classList.add("checked");
    }

    request_favorite_deck(id);
}

// Visibility checker
var isVisible = false;

function hide_form() {
    // Checks to see if element is visible
    if (isVisible) {
        // Sets visibility checker to false
        isVisible = false;
        // Grabs div element
        var element = document.getElementById("container");
        // Actually sets its visibility to hidden
        element.style.visibility = "hidden";
    }
}

function show_form() {
    // Checks to see if element is not visible
    if (!isVisible) {
        // Reset all of the fields
        document.getElementById("add-form").reset();
        // Sets form visibility checker to true
        isVisible = true;
        // Actually sets its visibility to visible
        document.getElementById("container").style.visibility = "visible";

        // Get the title text box and set focus to it that way
        // the user doesn't need to click it
        document.getElementById("title").focus();
    }
    console.log("success");
}
