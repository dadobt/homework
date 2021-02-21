package com.dadobt.homework.service;

import com.dadobt.homework.dto.MessageDtoRequest;
import com.dadobt.homework.dto.MessageDtoResponse;

import java.util.List;

public interface MessageService {

    MessageDtoResponse findMessageById(Long id);

    MessageDtoResponse createMessage(MessageDtoRequest messageDtoRequest, String name);

    MessageDtoResponse updateMessage(Long id, MessageDtoRequest messageRequest, String name);

    void deleteMessage(Long id, String name);

    List<MessageDtoResponse> findAllByUser(String username);

    List<MessageDtoResponse> findAll();
}
