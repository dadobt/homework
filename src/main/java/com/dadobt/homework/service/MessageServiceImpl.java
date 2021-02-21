package com.dadobt.homework.service;

import com.dadobt.homework.dto.MessageDtoRequest;
import com.dadobt.homework.dto.MessageDtoResponse;
import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.entity.Message;
import com.dadobt.homework.exceptions.ForbiddenException;
import com.dadobt.homework.exceptions.ResourceNotFoundException;
import com.dadobt.homework.repository.ApplicationUserRepository;
import com.dadobt.homework.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Override
    public MessageDtoResponse findMessageById(Long id) {
        Message message = messageRepository.findOneById(id);
        if (message == null) {
            throw new ResourceNotFoundException("Message Not Found");
        }
        return createMessageResponseDtoFromMessageModel(message);
    }

    @Override
    public MessageDtoResponse createMessage(MessageDtoRequest messageDtoRequest, String username) {
        ApplicationUser user = applicationUserRepository.findByUsername(username);
        Message message = createMessageEntityFromMessageRequest(messageDtoRequest, user);
        Message save = messageRepository.save(message);
        return createMessageResponseDtoFromMessageModel(save);
    }

    @Override
    public MessageDtoResponse updateMessage(Long id, MessageDtoRequest messageRequest, String name) {
        Message message = messageRepository.findOneById(id);
        if (message == null) {
            throw new ResourceNotFoundException("Message not found");
        }
        if (!message.getUser().getUsername().equals(name)) {
            throw new ForbiddenException("You don`t have privileges to modify the message");
        }
        message.setTitle(messageRequest.getTitle());
        message.setMessage(messageRequest.getMessage());
        Message updatedMessage = messageRepository.save(message);
        return createMessageResponseDtoFromMessageModel(updatedMessage);
    }

    @Override
    public void deleteMessage(Long id, String name) {
        Message message = messageRepository.findOneById(id);
        if (message == null) {
            throw new ResourceNotFoundException("Message not found");
        }
        if (!message.getUser().getUsername().equals(name)) {
            throw new ForbiddenException("You don`t have privileges to delete the message");
        }
        messageRepository.delete(message);
    }

    @Override
    public List<MessageDtoResponse> findAllByUser(String username) {
        ApplicationUser user = applicationUserRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        List<Message> allMessagesFromUser = messageRepository.findAllByUser(user);
        if (allMessagesFromUser == null || allMessagesFromUser.isEmpty()) {
            throw new ResourceNotFoundException("Messages for that user not found");
        }
        List<MessageDtoResponse> messageDtoResponseList = new LinkedList<>();
        for (Message message : allMessagesFromUser) {
            messageDtoResponseList.add(createMessageResponseDtoFromMessageModel(message));
        }
        return messageDtoResponseList;
    }

    @Override
    public List<MessageDtoResponse> findAll() {
        List<Message> all = messageRepository.findAll();
        if (all.isEmpty()) {
            throw new ResourceNotFoundException("No Messages in the system");
        }
        List<MessageDtoResponse> messageDtoResponseList = new LinkedList<>();
        for (Message message : all) {
            messageDtoResponseList.add(createMessageResponseDtoFromMessageModel(message));
        }
        return messageDtoResponseList;
    }

    protected MessageDtoResponse createMessageResponseDtoFromMessageModel(Message model) {
        MessageDtoResponse messageDtoResponse = new MessageDtoResponse();
        messageDtoResponse.setId(model.getId());
        messageDtoResponse.setTitle(model.getTitle());
        messageDtoResponse.setMessage(model.getMessage());
        return messageDtoResponse;
    }

    protected Message createMessageEntityFromMessageRequest(MessageDtoRequest messageDtoRequest, ApplicationUser user) {
        Message message = new Message();
        message.setUser(user);
        message.setTitle(messageDtoRequest.getTitle());
        message.setMessage(messageDtoRequest.getMessage());
        return message;
    }

}
