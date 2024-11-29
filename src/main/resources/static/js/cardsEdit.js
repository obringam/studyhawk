var loadedDeckJSON; // The loaded deck info JSON at any given time
var loadedCardsJSON; // The loaded cards JSON at any given time
var deleteList = []; // List of cards to delete
var changesSaved = true;
var uniqueAddID = 1;

// Runs on startup of page
function loadPage() {
    loadDeckInfo();
    loadCards();

    const title = document.getElementById("deck-title");
    const description = document.getElementById("deck-description");
    title.addEventListener("input", function() {
        updateDeck();
    });
    description.addEventListener("input", function() {
        updateDeck();
    });
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
    term.classList.add("card-input", "term-input");
    term.maxLength = 100;
    term.value = card["term"];
    term.placeholder = "Term";
    term.addEventListener("input", function () {
        updateCard(cardCon);
    });

    const definition = document.createElement("input");
    definition.classList.add("card-input", "definition-input");
    definition.maxLength = 500;
    definition.value = card["definition"];
    definition.placeholder = "Definition";
    definition.addEventListener("input", function () {
        updateCard(cardCon);
    });

    // Add Image Button
    const addImgBtn = document.createElement("button");
    addImgBtn.classList.add("imgbtn");

    const imgIcon = document.createElement("img");
    imgIcon.classList.add("img-icon");
    imgIcon.setAttribute("src", "/images/image.svg");
    addImgBtn.appendChild(imgIcon);

    // Hidden file input for uploading images
    const fileInput = document.createElement("input");
    fileInput.classList.add("image-input");
    fileInput.type = "file";
    fileInput.accept = ".png, .jpeg, .jpg"; // Restrict file types
    fileInput.style.display = "none";

    // Open file dialog when clicking the image button
    addImgBtn.addEventListener("click", function () {
        fileInput.click();
    });

    // Handle file upload and preview
    fileInput.addEventListener("change", function () {
        const file = fileInput.files[0];
        if (file) {
            const fileSizeInBytes = file.size; // File size in bytes
            const fileSizeInKB = (fileSizeInBytes / 1024).toFixed(2); // Convert bytes to KB
            const fileSizeInMB = (fileSizeInBytes / (1024 * 1024)).toFixed(2); // Convert bytes to MB

            if (fileSizeInMB > 8) {
                alert('Image exceeds maximum size of 8 MB');
                return;
            }

            const reader = new FileReader();
            reader.onload = function (e) {
                imgIcon.src = e.target.result; // Update the image preview
                updateCard(cardCon); // Optionally update card data
                removeImgBtn.style.display = "inline"; // Show remove button
            };
            reader.readAsDataURL(file);
        }
    });

    // Remove Image Button
    const removeImgBtn = document.createElement("button");
    removeImgBtn.classList.add("remove-img-btn");
    removeImgBtn.textContent = "Remove Image";
    removeImgBtn.style.display = "none"; // Initially hidden
    removeImgBtn.addEventListener("click", function () {
        imgIcon.src = "/images/image.svg"; // Reset to default icon
        fileInput.value = ""; // Clear file input
        removeImgBtn.style.display = "none"; // Hide remove button
        removeImage(cardCon);
    });

    // Load existing images
    if (card["image"] != null) { // If there is already a loaded image
        imgIcon.src = "data:image/jpeg;base64, " + card["image"];
        removeImgBtn.style.display = "inline";
    }

    // Delete Button
    const deleteButton = document.createElement("button");
    deleteButton.classList.add("deleteBtn");
    deleteButton.onclick = function () {
        deleteCard(cardCon);
    };
    const deleteText = document.createTextNode("X");

    deleteButton.appendChild(deleteText);
    cardCon.appendChild(cardNum);
    cardCon.appendChild(br1);
    cardCon.appendChild(term);
    cardCon.appendChild(definition);
    cardCon.appendChild(addImgBtn);
    cardCon.appendChild(fileInput); // Hidden input for image upload
    cardCon.appendChild(removeImgBtn); // Add remove image button
    cardCon.appendChild(deleteButton);
    cardList.appendChild(cardCon);
}

// Returns an image URL from the byte data
function createImageURL(bytes) {

    // Convert the byte array to a Blob
    const blob = new Blob([bytes], { type: 'image/png' });

    // Generate a URL for the Blob
    const imageUrl = URL.createObjectURL(blob);

    console.log(imageUrl);

    return imageUrl;
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

// Add a card to the page
function addCard() {

    // Create a unique add ID
    var newCardID = "add-" + uniqueAddID;
    uniqueAddID++;

    var newCard = {
        cardID: newCardID,
        term: "",
        definition: "",
        favorite: false,
        image: null
    };

    // Add the new card to the JSON list
    loadedCardsJSON[loadedCardsJSON.length] = newCard;

    // Redisplay the cards
    displayCards();

    changesNotSavedAction();
}

// Delete a card from the page and queue it for deletion when changes are saved
function deleteCard(card) {

    // Get the actual card ID from the div element ID
    const extractedID = card.id.substring(5);

    // Find the card in the JSON list if it exists, queue for deletion
    for (let i = loadedCardsJSON.length - 1; i >= 0; i--) {
        let cardJSON = loadedCardsJSON[i];

        if (cardJSON["cardID"] == extractedID) {

            // Don't add to the delete list if it is not already in the database
            // AKA a card which was added without saving yet
            const potentialAddID = extractedID.substring(0, 3);
            if (potentialAddID != 'add') {
                deleteList[deleteList.length] = cardJSON;
            }

            loadedCardsJSON.splice(i, 1);
            break;
        }
    }

    // Redisplay the cards
    displayCards();

    changesNotSavedAction();
}

// Triggered when deck info is updated
function updateDeck() {
    changesNotSavedAction();
}

// Update card in the JSON list if it is modified on the page
function updateCard(card) {
    // Get the actual card ID from the div element ID
    const extractedID = card.id.substring(5);

    // Get the card JSON from the JSON list for the given ID
    const cardJSON = getCardJSONByID(extractedID);

    // If the card is not found
    if (cardJSON == null) {
        return;
    }

    // Get term and definition from page
    const cardTerm = card.querySelector('.term-input').value;
    const cardDefinition = card.querySelector('.definition-input').value;
    const fileInput = card.querySelector('.image-input').value;
    const imagePreview = card.querySelector('.img-icon').src;

    // Assign to term and definition in JSON
    cardJSON["term"] = cardTerm;
    cardJSON["definition"] = cardDefinition;
    if (fileInput != "") { // If a image file is selected
        cardJSON["image"] = imagePreview.split("base64,")[1]; // Update the cardJSON with the new image
    }

    changesNotSavedAction();
}

// Remove an image from a card
function removeImage(card) {
    // Get the actual card ID from the div element ID
    const extractedID = card.id.substring(5);

    // Get the card JSON from the JSON list for the given ID
    const cardJSON = getCardJSONByID(extractedID);

    cardJSON["image"] = null
    changesNotSavedAction();
}

// Saves any modifications to the deck
function saveChanges() {

    const cardsOnPage = document.querySelectorAll('#editor-container div');

    const addList = []; // List of cards to add
    const editList = []; // List of cards to edit

    // Assign each card on the page to a certain operation
    // Note that the cards to be deleted are already assigned to a global variable
    cardsOnPage.forEach((card) => {

        const extractedID = card.id.substring(5); // ID without 'card-' prefix
        const potentialAddID = extractedID.substring(0, 3); // Will result in add if a new card

        const cardTerm = card.querySelector('.term-input').value;
        const cardDefinition = card.querySelector('.definition-input').value;
        const fileInput = card.querySelector('.image-input').value;
        var cardImage;
        if (fileInput != "") { // If an image file is selected
            cardImage = card.querySelector('.img-icon').src.split("base64,")[1];
        } else {
            cardImage = null;
        }

        if (potentialAddID == 'add') {
            // If it is a new card, create and push to add list
            const cardJSON = {
                term: cardTerm,
                definition: cardDefinition,
                image: cardImage
            }
            addList.push(cardJSON);
        } else {
            // If it is an existing card, push to edit list
            // Get the card JSON from the JSON list for the given ID
            const cardJSON = getCardJSONByID(extractedID);
            if (cardJSON != null) {
                editList.push(cardJSON);
            }
        }
    });


    // Debug statements
    // console.log("addList");
    // console.log(addList);
    // console.log("deleteList");
    // console.log(deleteList);
    // console.log("editList")
    // console.log(editList);

    const requests = []; // An array of the requests to send

    // Create new cards
    if (addList != null && addList.length != 0) {
        requests.push($.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/cards/req/create/" + getDeckID(),
            data: JSON.stringify(addList),
            dataType: 'json',
            cache: false,
            timeout: 600000
        }));
    }

    // Delete Cards
    if (deleteList != null && deleteList.length != 0) {
        requests.push($.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/cards/req/delete",
            data: JSON.stringify(deleteList),
            dataType: 'json',
            cache: false,
            timeout: 600000
        }));
    }

    // Edit cards
    if (editList != null && editList.length != 0) {
        console.log(editList);
        requests.push($.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/cards/req/edit",
            data: JSON.stringify(editList),
            dataType: 'json',
            cache: false,
            timeout: 600000
        }));
    }

    // Edit deck info
    const title = document.getElementById("deck-title");
    const decription = document.getElementById("deck-description");
    var deckJSON = {
        deckID: getDeckID(),
        title: title.value,
        description: decription.value
    };
    requests.push($.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/decks/update",
        data: JSON.stringify(deckJSON),
        dataType: 'json',
        cache: false,
        timeout: 600000
    }));

    // Send all requests
    $.when.apply($, requests)
        .done((...responses) => {
            console.log("SUCCESSFUL REQUESTS: ", responses)
            loadCards();
            loadDeckInfo();
            changesSavedAction();
        })
        .fail((jqXHR, textStatus, errorThrown) => {
            console.error("One of the requests failed:", textStatus, errorThrown);
            changesNotSavedAction();
        });

}

// Get the card with a certain ID from the JSON list
function getCardJSONByID(ID) {
    for (let i = loadedCardsJSON.length - 1; i >= 0; i--) {
        let cardJSON = loadedCardsJSON[i];

        if (cardJSON["cardID"] == ID) {
            return cardJSON;
        }
    }
    return null;
}

// Action to take when changes are no longer saved
function changesNotSavedAction() {
    if (changesSaved) {
        const saveStatus = document.querySelector('#changes-saved-label');
        saveStatus.innerHTML = "**Your changes are not saved!**";
        changesSaved = false;
    }
}

// Action to take when changes are saved now
function changesSavedAction() {
    if (!changesSaved) {
        const saveStatus = document.querySelector('#changes-saved-label');
        saveStatus.innerHTML = "Your changes are saved";
        changesSaved = true;
    }
}