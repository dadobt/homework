package com.dadobt.homework.controler;

import com.dadobt.homework.dto.AuthenticationRequest;
import com.dadobt.homework.dto.AuthenticationResponse;
import com.dadobt.homework.service.MyUserDetailsService;
import com.dadobt.homework.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class AuthenticateControllerTest {

    public static final String TEST_USER = "TEST_USER";
    public static final String STRONG_PASS = "STRONG PASS";
    public static final String TOKEN = "SomeRandomToken";

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtTokenUtil;

    @Mock
    private  UserDetails userDetails;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private AuthenticateController authenticateControllerMock;

    @InjectMocks
    private  AuthenticateController authenticateController;

    @BeforeEach
    public void Setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void givenUserIsCreated_whenCreateAuthenticationTokenIsCalledWithCorrectCredentials_thenHttpStatusOkAndValidTokenIsReturned() throws Exception {
        //Arrange
        AuthenticationRequest authenticationRequest
                = new AuthenticationRequest(TEST_USER,STRONG_PASS);

        AuthenticationResponse expectedResponse = new AuthenticationResponse(TOKEN);


        doNothing().when(authenticateControllerMock).authenticate(anyString(),anyString());
        when(userDetailsService.loadUserByUsername(authenticationRequest.getUsername())).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(any())).thenReturn(TOKEN);

        //act
        ResponseEntity<?> actualResponse = authenticateController.createAuthenticationToken(authenticationRequest);

        //assert
        assertEquals(HttpStatus.OK,actualResponse.getStatusCode());
        AuthenticationResponse body = (AuthenticationResponse) actualResponse.getBody();
        assertEquals(expectedResponse.getJwt(),body.getJwt());

    }
}