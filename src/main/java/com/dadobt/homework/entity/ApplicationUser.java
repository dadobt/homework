package com.dadobt.homework.entity;


import lombok.Data;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    private String username;

    @NonNull
    private String password;

    public ApplicationUser() {
    }

    public ApplicationUser(String username, String encodedPassword) {
        this.username = username;
        this.password = encodedPassword;
    }
}