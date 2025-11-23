package com.saiteja.flightbookingwebfluxmongo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private UserRole role;

}
