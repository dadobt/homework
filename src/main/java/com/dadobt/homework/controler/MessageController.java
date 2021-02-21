package com.dadobt.homework.controler;

import com.dadobt.homework.dto.MessageDtoRequest;
import com.dadobt.homework.dto.MessageDtoResponse;
import com.dadobt.homework.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/message/{id}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMessageById(@PathVariable Long id) {
        MessageDtoResponse messageById = messageService.findMessageById(id);
        return ResponseEntity.ok(messageById);
    }

    @RequestMapping(value ="/message",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMessage(@Valid @RequestBody MessageDtoRequest messageDtoRequest, Authentication authentication) {
        MessageDtoResponse messageDtoResponse = messageService.createMessage(messageDtoRequest, authentication.getName());
        return new ResponseEntity<>(messageDtoResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/message/{id}",method = RequestMethod.PATCH,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMessage(@PathVariable Long id, @Valid @RequestBody MessageDtoRequest messageRequest,
                                Authentication authentication) {
        MessageDtoResponse messageDtoResponse = messageService.updateMessage(id, messageRequest, authentication.getName());
        return ResponseEntity.ok(messageDtoResponse);

    }

    @RequestMapping(value = "/message/{id}",method = RequestMethod.DELETE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteMessage(@PathVariable Long id,Authentication authentication) {
           messageService.deleteMessage(id, authentication.getName());
            return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/message/user/{username}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllByUser(@PathVariable String username) {
        List<MessageDtoResponse> allByUser = messageService.findAllByUser(username);
        return ResponseEntity.ok(allByUser);
    }

    @RequestMapping(value = "/message/all",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {
        List<MessageDtoResponse> all = messageService.findAll();
        return ResponseEntity.ok(all);
    }
}
