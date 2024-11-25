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

    // Override default submit behavior for the share-form form
    $("#share-form").submit(function (event) {
        event.preventDefault();
        if (this.checkValidity()) {
            // Hide the form if validation passes
            hide_share_form();
            request_share_deck();
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
var loadedSharedDecksJSON; // The loaded decks JSON at any given time
var searchText; // The current search text entered and submitted at any given time

// Runs on startup of page
function loadPage() {
    loadDecks();
}

// Updates the search texts and redisplays the decks
function search(text) {
    // Update global var with search text from form (change to all lowercase)
    searchText = text.toLowerCase();
    // Call display decks to update the displayed decks
    displayDecks();
}

// Navigate to cards page for selected deck
function view_deck(id) {
    var extractedID = id.substring(5); // Remove prefix from deckID
    var newURL = window.location.origin + "/cards/" + extractedID;
    window.location.href = newURL; // Navigate to new URL
}

// Navigate to edit page for a deck's cards
function edit_deck(id) {
    var extractedID = id.substring(5);
    var newURL = window.location.origin + "/cards/edit/" + extractedID;
    window.location.href = newURL; // Navigate to new URL
}

// Displays the decks. The decks are filtered if a search text is entered.
function displayDecks() {
    const deckList = document.getElementById("deck-list");

    // Clear decks from deck list
    while (deckList.firstChild) {
        deckList.removeChild(deckList.lastChild);
    }

    var ownedDeckCount = 0; // Count the number of decks created

    /* YOUR DECKS */

    // Add your decks header
    const yourDeckHeader = document.createElement("h1");
    yourDeckHeader.classList.add("deck-label");
    const yourDeckText = document.createTextNode("Your Decks");
    yourDeckHeader.appendChild(yourDeckText);
    deckList.appendChild(yourDeckHeader);

    // For each deck returned, create an element and add to deck list (if favorite)
    for (let i = 0; i < loadedDecksJSON.length; i++) {
        let deck = loadedDecksJSON[i];

        if (deck["favorite"])
            ownedDeckCount += buildDeck(deck, deckList, false);
    }

    // For each deck returned, create an element and add to deck list (if not favorite)
    for (let i = 0; i < loadedDecksJSON.length; i++) {
        let deck = loadedDecksJSON[i];

        if (!deck["favorite"])
            ownedDeckCount += buildDeck(deck, deckList, false);
    }

    if (ownedDeckCount == 0) {
        const noDecksDiv = document.createElement("div");
        noDecksDiv.classList.add("no-decks-div");
        const noDecksPara = document.createElement("h4");

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

    /* SHARED DECKS */

    if (loadedSharedDecksJSON.length > 0) {
        // Add shared decks header
        const sharedDeckHeader = document.createElement("h1");
        sharedDeckHeader.classList.add("deck-label");
        const sharedDeckText = document.createTextNode("Shared Decks");
        sharedDeckHeader.appendChild(sharedDeckText);
        deckList.appendChild(sharedDeckHeader);
    }

    // For each deck returned, create an element and add to deck list
    for (let i = 0; i < loadedSharedDecksJSON.length; i++) {
        let deck = loadedSharedDecksJSON[i];
        buildDeck(deck, deckList, true);
    }
}

function buildDeck(deck, deckList, isShared) {
    // If search text is not empty, check if deck title or description
    // contains the text. If it doesn't, skip this deck.
    if (typeof searchText !== 'undefined') { // Undefined if no search has been entered yet
        if (searchText.length > 0) { // 0 if the search text is nothing
            // If search text isn't in the title or description
            if (!deck["title"].toLowerCase().includes(searchText)
                && !deck["description"].toLowerCase().includes(searchText)) {
                return 0;
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
    const editButton = document.createElement("Button");
    editButton.classList.add("deckbtn");
    const editText = document.createTextNode("Edit");
    editButton.onclick = function() {edit_deck(deckDiv.id)};
    const studyButton = document.createElement("Button");
    studyButton.classList.add("studybtn");
    studyButton.classList.add("deckbtn");
    studyButton.onclick = function() {view_deck(deckDiv.id)};
    const viewText = document.createTextNode("Study");
    const deleteButton = document.createElement("Button");
    deleteButton.classList.add("deletebtn");
    deleteButton.classList.add("deckbtn");
    var deleteText = document.createTextNode("Delete");
    if (isShared) {
        deleteButton.onclick = function() {
            if (confirm("Are you sure you want to remove " + deck["title"] + " from your shared decks?")) {
                request_remove_shared_deck(deleteButton, deck["title"], deckDiv.id);
            }
        };
        deleteText = document.createTextNode("Remove");
    } else {
        deleteButton.onclick = function() {
            if (confirm("Are you sure you want to delete " + deck["title"] + "?")) {
                request_delete_deck(deleteButton, deck["title"], deckDiv.id);
            }
        };
    }
    const favoriteSpan = document.createElement("Span");
    if (!isShared) {
        favoriteSpan.classList.add("fa");
        favoriteSpan.classList.add("fa-star");
        favoriteSpan.classList.add("favoritespan")
        // favoriteSpan.classList.add("deckbtn");
        if (deck["favorite"]) {
            favoriteSpan.classList.add("checked");
        }
        favoriteSpan.onclick = function() {toggle_favorite(favoriteSpan, deckDiv.id)};
    }
    const shareButton = document.createElement("Button");
    shareButton.classList.add("deckbtn");
    const shareText = document.createTextNode("Share");
    if (!isShared) {
        shareButton.onclick = function() {
            currentShareDeckID = deckDiv.id;
            currentShareDeckTitle = deck["title"];
            show_share_form();
        };
    }


    // Combine elements
    editButton.appendChild(editText);
    studyButton.appendChild(viewText);
    deleteButton.appendChild(deleteText);
    shareButton.appendChild(shareText);
    titleHeader.appendChild(title);
    titleDiv.appendChild(titleHeader);
    descriptionPara.appendChild(description);
    descriptionDiv.appendChild(descriptionPara);
    descriptionDiv.appendChild(deleteButton);
    descriptionDiv.appendChild(editButton);
    if (!isShared) {
        descriptionDiv.appendChild(shareButton);
    }
    descriptionDiv.appendChild(studyButton);
    if (!isShared) {
        descriptionDiv.appendChild(favoriteSpan);
    }
    deckDiv.appendChild(titleDiv);
    deckDiv.appendChild(hr);
    deckDiv.appendChild(descriptionDiv);

    // Add deck to deck list
    deckList.appendChild(deckDiv);
    return 1;
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
            loadedDecksJSON = data; // Assign deck data to global variable
            $.ajax({
                type: "GET",
                contentType: "application/json",
                url: "/decks/get/shared",
                cache: false,
                timeout: 600000,
                success: function (data2) {
                    loadedSharedDecksJSON = data2; // Assign deck data to global variable
                    displayDecks(); // Call function to display the decks
                },
                error: function (e) {
                    console.log("ERROR : ", e);
                }
            });
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
    requestData["isPublic"] = $("#public-box").is(':checked');

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

// Sends an ajax request to share a deck with a user
function request_share_deck() {
    var requestData = {}
    requestData["deckID"] = currentShareDeckID.substring(5); // Remove prefix from deckID
    requestData["title"] = currentShareDeckTitle;
    requestData["username"] = $("#username").val();

    $("#sharedsubmitbtn").prop("disabled", true);

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/decks/shared/share",
        data: JSON.stringify(requestData),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            alert("The deck was shared successfully!");
            $("#sharedsubmitbtn").prop("disabled", false);
        },
        error: function (e) {
            alert(e["responseJSON"]["message"]);
            $("#sharedsubmitbtn").prop("disabled", false);
        }
    });
}

// Sends an ajax request to delete a deck
function request_delete_deck(element, title, id) {

    var requestData = {}
    requestData["deckID"] = id.substring(5); // Remove prefix from deckID
    requestData["title"] = title;

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
            loadDecks();
        },
        error: function (e) {
            alert(e["responseJSON"]["message"]);
            element.disabled = false;
        }
    });
}

// Sends an ajax request to remove a shared deck for the user
function request_remove_shared_deck(element, title, id) {

    var requestData = {}
    requestData["deckID"] = id.substring(5); // Remove prefix from deckID
    requestData["title"] = title;

    element.disabled = true;

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/decks/shared/remove",
        data: JSON.stringify(requestData),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            loadDecks();
        },
        error: function (e) {
            alert(e["responseJSON"]["message"]);
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
    hide_share_form();
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
}

// Visibility checker
var shareIsVisible = false;
var currentShareDeckID = null;
var currentShareDeckTitle = null;

function show_share_form() {
    hide_form();

    if (!shareIsVisible) {
        // Sets visibility checker to false
        shareIsVisible = true;
        // Grabs div element
        var element = document.getElementById("share-container");
        // Actually sets its visibility to hidden
        element.style.visibility = "visible";

        document.getElementById("share-form").reset();
        document.getElementById("username").focus();
    }
}

function hide_share_form() {
    if (shareIsVisible) {
        // Sets visibility checker to false
        shareIsVisible = false;
        // Grabs div element
        var element = document.getElementById("share-container");
        // Actually sets its visibility to hidden
        element.style.visibility = "hidden";
    }
}