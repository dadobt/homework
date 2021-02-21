package com.dadobt.homework.service;

import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.repository.ApplicationUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class MyUserDetailsServiceTest {

    public static final String TEST_USER = "TestUser";
    public static final String PASSWORD = "Password";

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;


    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void loadUserByUsername() {

        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(TEST_USER);
        user.setPassword(PASSWORD);

        when(applicationUserRepository.findByUsername(TEST_USER)).thenReturn(user);

        UserDetails actualUserDetails = myUserDetailsService.loadUserByUsername(TEST_USER);

        assertEquals(user.getUsername(),actualUserDetails.getUsername());
        assertEquals(user.getPassword(),actualUserDetails.getPassword());
        assertTrue(actualUserDetails.isAccountNonExpired());
        assertTrue(actualUserDetails.isAccountNonLocked());
        assertTrue(actualUserDetails.isCredentialsNonExpired());
        assertTrue(actualUserDetails.isEnabled());

    }

    @Test
    void loadUserByUsernameThrowsUsernameNotFoundException() {

        when(applicationUserRepository.findByUsername(TEST_USER)).thenReturn(null);

        UsernameNotFoundException usernameNotFoundException = assertThrows(UsernameNotFoundException.class, () -> {
            myUserDetailsService.loadUserByUsername(TEST_USER);
        });

        assertEquals(String.format("Username: '%s' not found", TEST_USER),usernameNotFoundException.getMessage());
        verify(applicationUserRepository,times(1)).findByUsername(anyString());

    }
}