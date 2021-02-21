package com.dadobt.homework.controler;

import com.dadobt.homework.dto.AuthenticationRequest;
import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.repository.ApplicationUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UsersControllerTest {

    public static final String SUPER_SECRET_PASSWORD = "SuperSecretPassword";
    public static final String NEW_USER = "NewUser";

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersController usersController;

    @BeforeEach()
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void givenThatUserIsNonExistent_whenUserControllerCreateWithValidBodyIsCalled_thenUserIsCreated() {

        //arrange
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(NEW_USER);
        authenticationRequest.setPassword(SUPER_SECRET_PASSWORD);

        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(NEW_USER);
        user.setPassword(SUPER_SECRET_PASSWORD);

        when(applicationUserRepository.findByUsername(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn(SUPER_SECRET_PASSWORD);
        when(applicationUserRepository.save(any(ApplicationUser.class))).thenReturn(user);

        //act
        ResponseEntity<?> responseEntity = usersController.create(authenticationRequest);

        //assert
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        verify(applicationUserRepository).findByUsername(NEW_USER);
        verify(passwordEncoder,times(1)).encode(SUPER_SECRET_PASSWORD);
        verify(applicationUserRepository,times(1)).save(any(ApplicationUser.class));

    }


    @Test
    void givenThatUserExists_whenUserControllerCreateWithTheSameUsernameIsCalled_thenHttpCONFICTIsReturned() {

        //arrange
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(NEW_USER);
        authenticationRequest.setPassword(SUPER_SECRET_PASSWORD);
        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(NEW_USER);
        user.setPassword(SUPER_SECRET_PASSWORD);

        when(applicationUserRepository.findByUsername(anyString())).thenReturn(user);

        //act
        ResponseEntity<?> responseEntity = usersController.create(authenticationRequest);

        //assert
        assertEquals(HttpStatus.CONFLICT,responseEntity.getStatusCode());
        verify(applicationUserRepository).findByUsername(NEW_USER);
        verifyNoInteractions(passwordEncoder);
        verifyNoMoreInteractions(applicationUserRepository);

    }
}