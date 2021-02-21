package com.dadobt.homework.controler;

import com.dadobt.homework.dto.MessageDtoRequest;
import com.dadobt.homework.dto.MessageDtoResponse;
import com.dadobt.homework.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    public static final String TEST_TITLE = "Test Title";
    public static final String TEST_MESSAGE = "Test Message";

    @Mock
    private MessageService messageService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private  MessageController messageController;
    MessageDtoResponse expectedMessageDtoResponse = new MessageDtoResponse();
    MessageDtoRequest messageDtoRequest = new MessageDtoRequest();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        expectedMessageDtoResponse.setId(1L);
        expectedMessageDtoResponse.setTitle(TEST_TITLE);
        expectedMessageDtoResponse.setMessage(TEST_MESSAGE);
        messageDtoRequest.setTitle(TEST_TITLE);
        messageDtoRequest.setMessage(TEST_MESSAGE);
    }

    @Test
    void givenUserIsCreatedAndMessageWithIdIsCreated_whenMessageControllerGetMessageByIdIsCalled_thenHttpStatusOKAndMessageDtoResponseIsReturned() {
        //arrange
        when(messageService.findMessageById(anyLong())).thenReturn(expectedMessageDtoResponse);

        //act
        ResponseEntity<?> messageById = messageController.getMessageById(1L);

        //assert
        assertEquals(HttpStatus.OK,messageById.getStatusCode());
        assertEquals(expectedMessageDtoResponse,messageById.getBody());
        verify(messageService,times(1)).findMessageById(anyLong());
    }

    @Test
    void givenUserIsCreated_whenMessageControllerCreateMessageWithAValidMessageBodyIsCalled_thenHttpStatusCREATEDAndMessageDtoResponseIsReturned() {

        //arrange
        when(messageService.createMessage(any(),any())).thenReturn(expectedMessageDtoResponse);

        //act
        ResponseEntity<?> message = messageController.createMessage(messageDtoRequest, authentication);

        //assert
        assertEquals(HttpStatus.CREATED,message.getStatusCode());
        assertEquals(expectedMessageDtoResponse,message.getBody());

    }

    @Test
    void givenUserIsCreated_whenMessageControllerUpdateMessageWithValidIDAndValidMessageDtoRequestIsCalled_thenHttpStatusOKAndMessageDtoResponseIsReturned() {
        //arrange
        when(messageService.updateMessage(anyLong(),any(MessageDtoRequest.class),any())).thenReturn(expectedMessageDtoResponse);

        //act
        ResponseEntity<?> actualResponse = messageController.updateMessage(1L, messageDtoRequest, authentication);

        //assert
        assertEquals(HttpStatus.OK,actualResponse.getStatusCode());
        assertEquals(expectedMessageDtoResponse,actualResponse.getBody());
    }

    @Test
    void givenUserIsCreated_whenMessageControllerDeleteMessageWithValidID_thenHttpStatusNO_CONTENTIsReturned() {

        //arrange
        doNothing().when(messageService).deleteMessage(anyLong(),any());

        //act
        ResponseEntity<?> actualResponse = messageController.deleteMessage(1L, authentication);

        //assert
        assertEquals(HttpStatus.NO_CONTENT,actualResponse.getStatusCode());
        verify(messageService,times(1)).deleteMessage(anyLong(),any());
    }

    @Test
    void givenUserAndMessagesForUserAreCreated_whenMessageControllerGetAllMessagesByUsernameIsCalled_thenAllMessagesForTheUserAreReturned() {

        //arrange
        List<MessageDtoResponse> expectedList = new LinkedList<>();
        expectedList.add(expectedMessageDtoResponse);
        when(messageService.findAllByUser(anyString())).thenReturn(expectedList);

        //act
        ResponseEntity<?> actualResponse = messageController.findAllByUser("randomUsername");

        //assert
        assertEquals(HttpStatus.OK,actualResponse.getStatusCode());
        assertEquals(expectedList,actualResponse.getBody());
        verify(messageService,times(1)).findAllByUser(anyString());
    }

    @Test
    void givenUserAndMessagesAreCreated_whenMessageControllerFindAllIsCalled_thenAllMessagesAreReturned(){
        //arrange
        List<MessageDtoResponse> expectedList = new LinkedList<>();
        expectedList.add(expectedMessageDtoResponse);
        when(messageService.findAll()).thenReturn(expectedList);

        //act
        ResponseEntity<?> actualResponse = messageController.findAll();

        //assert
        assertEquals(HttpStatus.OK,actualResponse.getStatusCode());
        assertEquals(expectedList,actualResponse.getBody());
        verify(messageService,times(1)).findAll();
    }
}