package com.dadobt.homework.controler;

import com.dadobt.homework.dto.AuthenticationRequest;
import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UsersController {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody AuthenticationRequest createUserRequest) {
        String username = createUserRequest.getUsername();

        ApplicationUser byUsername = applicationUserRepository.findByUsername(username);
        if (byUsername != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        String password = createUserRequest.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        applicationUserRepository.save(new ApplicationUser(username, encodedPassword));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
