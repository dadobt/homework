package com.dadobt.homework.integartiontests;

import com.dadobt.homework.dto.MessageDtoRequest;
import com.dadobt.homework.dto.MessageDtoResponse;
import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.entity.Message;
import com.dadobt.homework.integartiontests.configuration.IntegrationTest;
import com.dadobt.homework.repository.ApplicationUserRepository;
import com.dadobt.homework.repository.MessageRepository;
import com.dadobt.homework.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MessageControllerIT extends IntegrationTest {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final String MESSAGE_ENDPOINT = "/message";
    private static final String MESSAGE_USER_ENDPOINT = "/message/user";
    String url;
    String jwt;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Before
    public void setUp() {
        url = getBaseUrl();
        deleteAllApplicationUsers();
        createApplicationUser();
        jwt = authenticateUser();
    }

    @After
    public void cleanUp() {
        deleteAllMessages();
    }

    @Test
    public void givenMessageIsAlreadyCreated_whenMessageGetByIdEndpointIsCalled_thenMessageIsReturned() {
        MessageDtoRequest messageDtoRequest = createMessageDtoRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("Bearer ").append(jwt);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", sb.toString());
        headers.set("Content-Type", "application/json");
        HttpEntity<MessageDtoRequest> postRequest = new HttpEntity<>(messageDtoRequest, headers);

        ResponseEntity<MessageDtoResponse> postResponseEntity =
                restTemplate.postForEntity(url + MESSAGE_ENDPOINT, postRequest, MessageDtoResponse.class);
        assertEquals(postResponseEntity.getStatusCode(), HttpStatus.CREATED);
        MessageDtoResponse postResponseMessage = postResponseEntity.getBody();

        HttpEntity<MessageDtoRequest> getRequest = new HttpEntity<>(headers);
        ResponseEntity<MessageDtoResponse> getResponseEntity =
                restTemplate.exchange(url + MESSAGE_ENDPOINT + "/" + postResponseMessage.getId(), HttpMethod.GET, getRequest, MessageDtoResponse.class);
        assertEquals(getResponseEntity.getStatusCode(), HttpStatus.OK);
        MessageDtoResponse getResponseMessage = getResponseEntity.getBody();

        assertNotNull(getResponseMessage);
        assertEquals(getResponseMessage, postResponseMessage);
    }
    
    @Test
    public void givenMessageNeedsToBeCreated_whenMessageCreateEndpointIsCalled_thenMessageIsCreatedAndSavedInDB() {
        MessageDtoRequest messageDtoRequest = createMessageDtoRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("Bearer ").append(jwt);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", sb.toString());
        headers.set("Content-Type", "application/json");
        HttpEntity<MessageDtoRequest> request = new HttpEntity<>(messageDtoRequest, headers);

        ResponseEntity<MessageDtoResponse> responseEntity = restTemplate.postForEntity(url + MESSAGE_ENDPOINT, request, MessageDtoResponse.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
        MessageDtoResponse responseMessage = responseEntity.getBody();

        Message message = getMessageById(responseMessage.getId());
        assertNotNull(message);
        assertEquals(message.getMessage(), responseMessage.getMessage());
        assertEquals(message.getId(), responseMessage.getId());
        assertEquals(message.getTitle(), responseMessage.getTitle());
    }

    @Test
    public void givenMessageIsAlreadyCreated_whenMessagePatchByIdEndpointIsCalled_thenMessageIsUpdated() throws IOException, InterruptedException {
        MessageDtoRequest messageDtoRequest = createMessageDtoRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("Bearer ").append(jwt);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", sb.toString());
        headers.set("Content-Type", "application/json");
        HttpEntity<MessageDtoRequest> postRequest = new HttpEntity<>(messageDtoRequest, headers);

        ResponseEntity<MessageDtoResponse> postResponseEntity =
                restTemplate.postForEntity(url + MESSAGE_ENDPOINT, postRequest, MessageDtoResponse.class);
        assertEquals(postResponseEntity.getStatusCode(), HttpStatus.CREATED);
        MessageDtoResponse postResponseMessage = postResponseEntity.getBody();

        MessageDtoRequest patchMessageDtoRequest = createMessageDtoRequest();
        patchMessageDtoRequest.setTitle("some other title");
        patchMessageDtoRequest.setMessage("some other message");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(patchMessageDtoRequest);

        String patchUrl = url + MESSAGE_ENDPOINT + "/" + postResponseMessage.getId();
        HttpRequest patchRequest = HttpRequest.newBuilder()
                .uri(URI.create(patchUrl))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Authorization", sb.toString())
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(patchRequest, HttpResponse.BodyHandlers.ofString());
        MessageDtoResponse patchResponseMessage = objectMapper.readValue(response.body(), MessageDtoResponse.class);

        Message message = getMessageById(postResponseMessage.getId());
        assertNotNull(message);
        assertEquals(message.getId(), patchResponseMessage.getId());
        assertEquals(message.getMessage(), patchResponseMessage.getMessage());
        assertEquals(message.getTitle(), patchResponseMessage.getTitle());
    }

    @Test
    public void givenMessageIsCreated_whenMessageDeleteEndpointIsCalled_thenMessageIsDeletedFromDB() {
        MessageDtoRequest messageDtoRequest = createMessageDtoRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("Bearer ").append(jwt);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", sb.toString());
        headers.set("Content-Type", "application/json");
        HttpEntity<MessageDtoRequest> request = new HttpEntity<>(messageDtoRequest, headers);

        ResponseEntity<MessageDtoResponse> responseEntity = restTemplate.postForEntity(url + MESSAGE_ENDPOINT, request, MessageDtoResponse.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
        MessageDtoResponse postResponseMessage = responseEntity.getBody();

        HttpEntity<MessageDtoRequest> deleteRequest = new HttpEntity<>(headers);
        ResponseEntity<MessageDtoResponse> getResponseEntity =
                restTemplate.exchange(url + MESSAGE_ENDPOINT + "/" + postResponseMessage.getId(), HttpMethod.DELETE, deleteRequest, MessageDtoResponse.class);
        assertEquals(getResponseEntity.getStatusCode(), HttpStatus.NO_CONTENT);

        Message message = getMessageById(postResponseMessage.getId());
        assertNull(message);
    }

    @Test
    public void givenUserAndMessagesAreCreated_whenGetMessagesByUsernameEndpointIsCalled_thenAllMessagesForThatUserAreReturned() {
        MessageDtoRequest messageDtoRequest = createMessageDtoRequest();
        MessageDtoRequest secondMessageDtoRequest = createMessageDtoRequest();

        StringBuilder sb = new StringBuilder();
        sb.append("Bearer ").append(jwt);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", sb.toString());
        headers.set("Content-Type", "application/json");
        HttpEntity<MessageDtoRequest> firstMessage = new HttpEntity<>(messageDtoRequest, headers);

        ResponseEntity<MessageDtoResponse> firstMessageResponse =
                restTemplate.postForEntity(url + MESSAGE_ENDPOINT, firstMessage, MessageDtoResponse.class);
        assertEquals(firstMessageResponse.getStatusCode(), HttpStatus.CREATED);

        HttpEntity<MessageDtoRequest> secondCreateRequest = new HttpEntity<>(secondMessageDtoRequest, headers);
        ResponseEntity<MessageDtoResponse> secondMessageResponse =
                restTemplate.postForEntity(url + MESSAGE_ENDPOINT, secondCreateRequest, MessageDtoResponse.class);
        assertEquals(secondMessageResponse.getStatusCode(), HttpStatus.CREATED);

        HttpEntity<MessageDtoRequest> getAllMessagesRequest = new HttpEntity<>(headers);
        ResponseEntity<MessageDtoResponse[]> getResponseEntity =
                restTemplate.exchange(url + MESSAGE_USER_ENDPOINT + "/someUsername", HttpMethod.GET, getAllMessagesRequest, MessageDtoResponse[].class);
        assertEquals(getResponseEntity.getStatusCode(), HttpStatus.OK);
        MessageDtoResponse[] messagesDtoResponseList = getResponseEntity.getBody();

        ApplicationUser applicationUser = getApplicationUserByUsername("someUsername");
        List<Message> messages = getMessagesByUser(applicationUser);
        assertNotNull(messages);
        assertEquals(2, messages.size());

        assertNotNull(messagesDtoResponseList);
        assertEquals(2, messagesDtoResponseList.length);
    }
}
