$(document).ready(function () {

    $("#add-form").submit(function (event) {
        event.preventDefault();
        request_add_deck();
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
                deleteButton.id = "deck-" + deck["deckID"];
                deleteButton.onclick = function() {request_delete_deck(deleteButton.id)};
                const deleteText = document.createTextNode("Delete");


                // Combine elements
                deleteButton.appendChild(deleteText);
                titleHeader.appendChild(title);
                titleDiv.appendChild(titleHeader);
                descriptionPara.appendChild(description);
                descriptionDiv.appendChild(descriptionPara);
                descriptionDiv.appendChild(deleteButton);
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

    $("#submit").prop("disabled", true);

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
            $("#submit").prop("disabled", false);
        },
        error: function (e) {
            console.log("ERROR : ", e);
            $("#submit").prop("disabled", false);
        }
    });
}

// Sends an ajax request to delete a deck
function request_delete_deck(id) {

    var requestData = {}
    requestData["deckID"] = id.substring(5); // Remove prefix from deckID

    $("#" + id).prop("disabled", true);

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
        }
    });
}