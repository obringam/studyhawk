package org.studyhawk.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.studyhawk.DatabaseHandler;
import org.studyhawk.Components.UserAccount;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class RegistrationController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserAccount user) {
        Map<String, String> response = new HashMap<>();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            DatabaseHandler.insertUser(user);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("message", user.getUsername() + " was registered as a user!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
