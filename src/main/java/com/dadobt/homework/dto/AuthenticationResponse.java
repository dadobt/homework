package com.dadobt.homework.dto;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    private String jwt;

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    private AuthenticationResponse(){}

    public String getJwt() {
        return jwt;
    }
}
