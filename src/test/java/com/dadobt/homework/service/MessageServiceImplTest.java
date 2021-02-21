package com.dadobt.homework.service;

import com.dadobt.homework.dto.MessageDtoRequest;
import com.dadobt.homework.dto.MessageDtoResponse;
import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.entity.Message;
import com.dadobt.homework.exceptions.ForbiddenException;
import com.dadobt.homework.exceptions.ResourceNotFoundException;
import com.dadobt.homework.repository.ApplicationUserRepository;
import com.dadobt.homework.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MessageServiceImplTest {

    public static final String TEST_TITLE = "Test Title";
    public static final String TEST_MESSAGE = "Test Message";
    public static final String TEST_USER = "TestUser";
    public static final String PASSWORD = "Password";
    public static final String UNAUTHORIZED_USERNAME = "unauthorizedUsername";

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    Message message = new Message();
    MessageDtoResponse expectedMessageDtoResponse= new MessageDtoResponse();
    MessageDtoRequest messageDtoRequest = new MessageDtoRequest();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        message.setId(1L);
        message.setTitle(TEST_TITLE);
        message.setMessage(TEST_MESSAGE);
        expectedMessageDtoResponse.setId(1L);
        expectedMessageDtoResponse.setTitle(TEST_TITLE);
        expectedMessageDtoResponse.setMessage(TEST_MESSAGE);
        messageDtoRequest.setTitle(TEST_TITLE);
        messageDtoRequest.setMessage(TEST_MESSAGE);
    }

    @Test
    void givenMessageIsCreated_whenMessageServiceFindOneByIdIsCalled_thenMessageWithThatIdIsReturned() {

        //arrange
        when(messageRepository.findOneById(anyLong())).thenReturn(message);

        //act
        MessageDtoResponse actualMessageDtoResponse = messageService.findMessageById(1L);

        //assert
        assertEquals(expectedMessageDtoResponse,actualMessageDtoResponse);
        verify(messageRepository,times(1)).findOneById(anyLong());

    }

    @Test
    void givenMessageIsCreated_whenMessageServiceFindOneByIdIsCalledWithNonExistentMessageId_thenMessageNotFoundIsReturned() {
        //arrange
        when(messageRepository.findOneById(anyLong())).thenReturn(null);

        //act
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            messageService.findMessageById(1L);
        });
        //assert
        assertEquals("Message Not Found",resourceNotFoundException.getMessage());
    }

    @Test
    void givenUserIsCreatedAndAuthenticated_whenMessageServiceCreateMessageIsCalled_thenMessageIsCreatedForThatUser() {
        //arrange
        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(TEST_USER);
        user.setPassword(PASSWORD);

        when(applicationUserRepository.findByUsername(anyString())).thenReturn(user);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        //act
        MessageDtoResponse actualMessageDtoResponse = messageService.createMessage(messageDtoRequest,TEST_USER);

        //assert
        assertEquals(expectedMessageDtoResponse,actualMessageDtoResponse);
        verify(messageRepository,times(1)).save(any(Message.class));
    }

    @Test
    void givenUserIsCreatedAndAuthenticated_whenMessageServiceUpdateMessageIsCalled_thenMessageIsUpdatedForThatUser() {
        //arrange
        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(TEST_USER);
        user.setPassword(PASSWORD);
        message.setUser(user);
        when(messageRepository.findOneById(anyLong())).thenReturn(message);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        //act
        MessageDtoResponse actualMessageDtoResponse = messageService.updateMessage(1L, messageDtoRequest, TEST_USER);

        //assert
        assertEquals(expectedMessageDtoResponse,actualMessageDtoResponse);
        verify(messageRepository,times(1)).save(any(Message.class));

    }

    @Test
    void givenUserIsCreatedAndAuthenticated_whenMessageServiceUpdateMessageIsCalledButMessageIdIsNotFound_thenMessageNotFoundIsReturned() {
        //arrange
        when(messageRepository.findOneById(anyLong())).thenReturn(null);

        //act
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            messageService.updateMessage(1L,messageDtoRequest,TEST_USER);
        });

        //assert
        assertEquals("Message not found",resourceNotFoundException.getMessage());

    }

    @Test
    void givenUserIsCreatedAndAuthenticated_whenMessageServiceUpdateMessageIsCalledButUserIsTryingToEditOtherUsersMessage_thenForbiddenExceptionIsReturned() {

        //arrange
        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(TEST_USER);
        user.setPassword(PASSWORD);
        when(messageRepository.findOneById(anyLong())).thenReturn(message);
        message.setUser(user);

        //act
        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class, () -> {
            messageService.updateMessage(1L,messageDtoRequest, UNAUTHORIZED_USERNAME);
        });

        //assert
        assertEquals("You don`t have privileges to modify the message",forbiddenException.getMessage());

    }

    @Test
    void givenUserAndMessagesAreCreated_whenMessageServiceDeleteMessageByIdIsCalled_thenMessageWithThatIdIsDeleted() {

        //arrange
        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(TEST_USER);
        user.setPassword(PASSWORD);
        message.setUser(user);
        when(messageRepository.findOneById(anyLong())).thenReturn(message);

        //act
        messageService.deleteMessage(1L,TEST_USER);

        verify(messageRepository,times(1)).delete(message);

    }

    @Test
    void givenUserAndMessagesAreCreated_whenMessageServiceDeleteMessageByIdIsCalledWithNonExistentMessageId_thenMessageNotFoundIsReturned() {
        //arrange
        when(messageRepository.findOneById(anyLong())).thenReturn(null);

        //act
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            messageService.deleteMessage(1L,TEST_USER);
        });

        //assert
        assertEquals("Message not found",resourceNotFoundException.getMessage());

    }

    @Test
    void givenUserAndMessagesAreCreated_whenMessageServiceDeleteMessageByIdIsCalledAndUserTriesToDeleteOtherUsersMessage_thenForbiddenExceptionIsReturned() {

        //arrange
        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(TEST_USER);
        user.setPassword(PASSWORD);
        when(messageRepository.findOneById(anyLong())).thenReturn(message);
        message.setUser(user);

        //act
        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class, () -> {
            messageService.deleteMessage(1L, UNAUTHORIZED_USERNAME);
        });

        //assert
        assertEquals("You don`t have privileges to delete the message",forbiddenException.getMessage());
    }


    @Test
    void givenUserAndMessagesAreCreated_whenMessageServiceFindAllByUserIsCalled_thenAllMessagesForThatUserAreReturned() {
        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(TEST_USER);
        user.setPassword(PASSWORD);

        LinkedList<Message> messageList = new LinkedList<>();
        messageList.add(message);

        LinkedList<MessageDtoResponse> expectedDtoList = new LinkedList<>();
        expectedDtoList.add(expectedMessageDtoResponse);

        when(applicationUserRepository.findByUsername(anyString())).thenReturn(user);
        when(messageRepository.findAllByUser(any(ApplicationUser.class))).thenReturn(messageList);

        List<MessageDtoResponse> actualDtoList = messageService.findAllByUser(TEST_USER);

        assertEquals(expectedDtoList,actualDtoList);
        verify(applicationUserRepository,times(1)).findByUsername(anyString());
        verify(messageRepository,times(1)).findAllByUser(any(ApplicationUser.class));

    }

    @Test
    void givenUserAndMessagesAreCreated_whenMessageServiceFindAllByUserIsCalledAndUserIsNonExistent_thenUserNotFoundIsReturned() {

        when(applicationUserRepository.findByUsername(anyString())).thenReturn(null);

        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            messageService.findAllByUser(UNAUTHORIZED_USERNAME);
        });

        assertEquals("User not found",resourceNotFoundException.getMessage());
    }


    @Test
    void givenUserAndMessagesAreCreated_whenMessageServiceFindAllByUserIsCalledAndMessagesAreNonExistent_thenMessagesForThatUserNotFoundIsReturned() {
        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(TEST_USER);
        user.setPassword(PASSWORD);

        when(applicationUserRepository.findByUsername(anyString())).thenReturn(user);
        when(messageRepository.findAllByUser(any(ApplicationUser.class))).thenReturn(Collections.emptyList());

        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            messageService.findAllByUser(TEST_USER);
        });

        assertEquals("Messages for that user not found",resourceNotFoundException.getMessage());
    }



    @Test
    void givenMessageModelIsCreated_whenMessageServiceCreateMessageResponseDtoFromMessageModelIsCalled_thenMessageDtoResponseIsCreatedAndReturned() {
        //arrange
        //all ready setup in before

        //act
        MessageDtoResponse actualMessageDtoResponse = messageService.createMessageResponseDtoFromMessageModel(message);

        //assert
        assertEquals(expectedMessageDtoResponse,actualMessageDtoResponse);
    }


    @Test
    void givenMessageRequestIsCreated_whenMessageServiceCreateMessageEntityFromMessageRequestIsCalled_thenMessageEntityIsCreatedAndReturned() {
        //arrange
        ApplicationUser user = new ApplicationUser();
        user.setId(123L);
        user.setUsername(TEST_USER);
        user.setPassword(PASSWORD);

        Message expectedMessage = new Message();
        expectedMessage.setTitle(TEST_TITLE);
        expectedMessage.setMessage(TEST_MESSAGE);
        expectedMessage.setUser(user);

        //act
        Message actualMessage = messageService.createMessageEntityFromMessageRequest(messageDtoRequest,user);

        //assert
        assertEquals(expectedMessage,actualMessage);

    }

    @Test
    void givenUserAndMessagesAreCreated_whenMessageServiceFindAll_thenAllMessagesAreReturned() {
        //arrange
        List<Message> messageList = new LinkedList<>();
         messageList.add(message);
        List<MessageDtoResponse> expectedList = new LinkedList<>();
        expectedList.add(expectedMessageDtoResponse);

        when(messageRepository.findAll()).thenReturn(messageList);

        //act
        List<MessageDtoResponse> actualResponse = messageService.findAll();


        //assert
        assertEquals(expectedList,actualResponse);

    }

    @Test
    void givenUserAndNoMessagesAreCreated_whenMessageServiceFindAll_thenNoMessagesAreReturned() {
        //arrange
        List<Message> messageList = new LinkedList<>();
        when(messageRepository.findAll()).thenReturn(messageList);

        //act
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            messageService.findAll();
        });

        //assert
        assertEquals("No Messages in the system",resourceNotFoundException.getMessage());

    }

}