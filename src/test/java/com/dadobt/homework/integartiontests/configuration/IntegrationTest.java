package com.dadobt.homework.integartiontests.configuration;

import com.dadobt.homework.HomeworkApplication;
import com.dadobt.homework.dto.AuthenticationRequest;
import com.dadobt.homework.dto.AuthenticationResponse;
import com.dadobt.homework.dto.MessageDtoRequest;
import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.entity.Message;
import com.dadobt.homework.repository.ApplicationUserRepository;
import com.dadobt.homework.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "integration")
@SpringBootTest(classes = HomeworkApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class IntegrationTest {

    private static final String HTTP_LOCALHOST = "http://localhost:";

    @Value("${server.port}")
    private int port;

    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    IntegrationTestConfigurationProperties testConfigurationProperties;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public ApplicationUserRepository applicationUserRepository;

    @Autowired
    private MessageRepository messageRepository;

    protected String getBaseUrl() {
        return HTTP_LOCALHOST + port;
    }

    public void createApplicationUser() {
        AuthenticationRequest applicationRequest = createAuthenticateRequest();
        restTemplate.postForEntity(getBaseUrl() + "/create", applicationRequest, ApplicationUser.class);
    }

    public String authenticateUser() {
        AuthenticationRequest applicationRequest = createAuthenticateRequest();
        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.postForEntity(getBaseUrl() + "/authenticate", applicationRequest, AuthenticationResponse.class);
        AuthenticationResponse response = responseEntity.getBody();
        return response.getJwt();
    }

    public AuthenticationRequest createAuthenticateRequest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("someUsername");
        authenticationRequest.setPassword("somePassword");
        return authenticationRequest;
    }

    public ApplicationUser getApplicationUserByUsername(String username) {
        return applicationUserRepository.findByUsername(username);
    }

    public void deleteAllApplicationUsers() {
        applicationUserRepository.deleteAll();
    }

    public Message getMessageById(Long id) {
        return messageRepository.findOneById(id);
    }

    public List<Message> getMessagesByUser(ApplicationUser applicationUser) {
        return messageRepository.findAllByUser(applicationUser);
    }

    public void deleteAllMessages() {
        messageRepository.deleteAll();
    }

    public MessageDtoRequest createMessageDtoRequest() {
        MessageDtoRequest messageDtoRequest = new MessageDtoRequest();
        messageDtoRequest.setTitle("IMPORTANT");
        messageDtoRequest.setMessage("Some important message");
        return messageDtoRequest;
    }
}
