package com.dadobt.homework.integartiontests;

import com.dadobt.homework.dto.AuthenticationRequest;
import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.integartiontests.configuration.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UsersControllerIT extends IntegrationTest {

    private static final String CREATE_ENDPOINT = "/create";
    String url;

    @Before
    public void setUp() {
        url = getBaseUrl();
    }

    @After
    public void cleanUp() {
        deleteAllApplicationUsers();
    }

    @Test
    public void givenUserNeedsToBeCreated_whenUserCreateEndpointIsCalled_thenUserIsCreatedAndSavedInDB() {
        AuthenticationRequest authenticationRequest = createAuthenticateRequest();
        ResponseEntity<ApplicationUser> responseEntity = restTemplate.postForEntity(url + CREATE_ENDPOINT, authenticationRequest, ApplicationUser.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);

        ApplicationUser savedApplicationUser = applicationUserRepository.findByUsername(authenticationRequest.getUsername());
        assertNotNull(savedApplicationUser);
        assertEquals(authenticationRequest.getUsername(), savedApplicationUser.getUsername());
    }

    @Test
    public void givenUserNeedsToBeCreated_whenUserCreateEndpointIsCalledWithSameUserTwice_thenSecondUserIsNotCreatedAndConflictIsReturned() {
        AuthenticationRequest authenticationRequest = createAuthenticateRequest();
        ResponseEntity<ApplicationUser> responseEntity = restTemplate.postForEntity(url + CREATE_ENDPOINT, authenticationRequest, ApplicationUser.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);

        ResponseEntity<ApplicationUser> responseEntity2 = restTemplate.postForEntity(url + CREATE_ENDPOINT, authenticationRequest, ApplicationUser.class);
        assertEquals(responseEntity2.getStatusCode(), HttpStatus.CONFLICT);
    }

}
