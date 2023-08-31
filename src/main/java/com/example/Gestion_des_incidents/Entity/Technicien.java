package com.example.Gestion_des_incidents.Entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document("technicien")
@Data
public class Technicien extends User{
    private List<Ticket> reparation = new ArrayList<Ticket>();
}
