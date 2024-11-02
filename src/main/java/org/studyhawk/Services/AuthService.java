package org.studyhawk.Services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.studyhawk.DatabaseHandler;
import org.studyhawk.Components.UserAccount;

@Service
public class AuthService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserAccount user = DatabaseHandler.getUserByUsername(username);
        if (user != null) {

            return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();

        } else {
            throw new UsernameNotFoundException(username);
        }

    }

}
