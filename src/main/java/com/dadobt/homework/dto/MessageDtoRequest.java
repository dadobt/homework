package com.dadobt.homework.dto;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
public class MessageDtoRequest implements Serializable {

    private String title;

    @NonNull
    private String message;

    public MessageDtoRequest(){}

}
