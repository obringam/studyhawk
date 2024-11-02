package org.studyhawk.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.studyhawk.DatabaseHandler;
import org.studyhawk.Components.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class RegistrationController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", consumes = "application/json")
    public UserAccount createUser(@RequestBody UserAccount user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        DatabaseHandler.insertUser(user);
        return user;
    }

}
