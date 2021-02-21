package com.dadobt.homework.integartiontests;

import com.dadobt.homework.dto.AuthenticationRequest;
import com.dadobt.homework.dto.AuthenticationResponse;
import com.dadobt.homework.integartiontests.configuration.IntegrationTest;
import com.dadobt.homework.service.MyUserDetailsService;
import com.dadobt.homework.util.JwtUtil;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthenticateControllerIT extends IntegrationTest {

    String url;
    private static final String AUTHENTICATE_ENDPOINT = "/authenticate";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Before
    public void setUp() {
        url = getBaseUrl();
        deleteAllApplicationUsers();
        createApplicationUser();
    }

    @Test
    public void givenAuthenticationNeedsToBeDone_whenAuthenticateEndpointIsCalled_thenUserIsAuthenticatedAndTokenIsGenerated() {
        AuthenticationRequest authenticateRequest = createAuthenticateRequest();
        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.postForEntity(url + AUTHENTICATE_ENDPOINT, authenticateRequest, AuthenticationResponse.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
}
