package com.dadobt.homework.service;

import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser user = applicationUserRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username: '%s' not found", username));
        }
        return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}