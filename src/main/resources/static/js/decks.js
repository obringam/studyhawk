$(document).ready(function () {

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

});

// Runs on startup of page
function loadPage() {
    loadDecks();
}

// Sends an ajax request to get decks and displays them
function loadDecks() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/decks/get",
        cache: false,
        timeout: 600000,
        success: function (data) {
            console.log("SUCCESS : ", data);

            const deckList = document.getElementById("deck-list");

            // Clear decks from deck list
            while (deckList.firstChild) {
                deckList.removeChild(deckList.lastChild);
            }

            // For each deck returned, create an element and add to deck list
            for(let i = 0; i < data.length; i++) {
                let deck = data[i];

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
                const deleteButton = document.createElement("Button");
                deleteButton.onclick = function() {request_delete_deck(deleteButton, deckDiv.id)};
                const deleteText = document.createTextNode("Delete");
                const favoriteSpan = document.createElement("Span");
                favoriteSpan.classList.add("fa");
                favoriteSpan.classList.add("fa-star");
                if (deck["favorite"]) {
                    favoriteSpan.classList.add("checked");
                }
                favoriteSpan.onclick = function() {toggle_favorite(favoriteSpan, deckDiv.id)};


                // Combine elements
                deleteButton.appendChild(deleteText);
                titleHeader.appendChild(title);
                titleDiv.appendChild(titleHeader);
                descriptionPara.appendChild(description);
                descriptionDiv.appendChild(descriptionPara);
                descriptionDiv.appendChild(deleteButton);
                descriptionDiv.appendChild(favoriteSpan);
                deckDiv.appendChild(titleDiv);
                deckDiv.appendChild(hr);
                deckDiv.appendChild(descriptionDiv);

                // Add deck to deck list
                deckList.appendChild(deckDiv);
            }
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
    }
    console.log("success");
}
