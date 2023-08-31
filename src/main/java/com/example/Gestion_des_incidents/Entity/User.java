package com.example.Gestion_des_incidents.Entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("users")
public class User {

    @Id
    private String id;
    private String name;
    private String lastname;
    private String number;
    private String email;
    private String password;
    private String role = "USER";
    private String adresse;
    public boolean valide = false;
    private String token_confirm;

    // @DBRef
    // private List<Ticket> tickets = new ArrayList<>();

    // public void addTicket(Ticket ticket) {
    // this.tickets.add(0, ticket);
    // }

}
