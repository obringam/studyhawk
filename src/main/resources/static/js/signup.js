$(document).ready(function () {

    // Override default submit behavior for the add-form form
    $("#signup-form").submit(function (event) {
        event.preventDefault();
        register_user();
    });

});

function navigate_to_login() {
    window.location.href = window.location.origin + "/login";
}

// Attempt to register a new user
function register_user() {
    const username = $("#username").val();
    const email = $("#email").val();
    const password = $("#password").val();
    const confirmPassword = $("#passwordcon").val();
    var requestData = {
        username,
        email,
        password
    }

    if (password == confirmPassword) {
        request_create_user(requestData);
    } else {
        alert("Please type the same password when confirming the password!");
    }

}

// Sends an ajax request to create a user account
function request_create_user(requestData) {
    $("#submit").prop("disabled", true);

    // const token = $("meta[name='_csrf']").attr("content");
    // const header = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/signup",
        data: JSON.stringify(requestData),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        // beforeSend: function(xhr) {
        //     xhr.setRequestHeader(header, token);
        // },
        success: function (data) {
            console.log(data);
            $("#submit").prop("disabled", false);
            navigate_to_login();
        },
        error: function (e) {
            console.log(e);
            alert(e["responseJSON"]["message"])
            $("#submit").prop("disabled", false);
        }
    });
}