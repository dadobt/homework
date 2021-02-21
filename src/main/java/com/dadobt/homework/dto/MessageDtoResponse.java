package com.dadobt.homework.dto;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
public class MessageDtoResponse implements Serializable {

    private Long id;

    private String title;

    @NonNull
    private String message;

    public MessageDtoResponse(){}


}
