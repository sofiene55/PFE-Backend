package com.example.Gestion_des_incidents.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("ticket")
public class Ticket {

    @Id
    private String id;
    private String title;
    private String date;
    private int urgence = 1;
    private String addresse;
    private int etat = 0;
    private String description;
    private String user_id;
    private String technicien_id;
    private String type;
    private String lieu;
    private String numero;
    private String categorie;
    private String status = "Nouveau";
    private String solution;
    private List<Tache> tache = new ArrayList<>();

    public void ajouterTache(Tache tache) {
        this.tache.add(0, tache);
    }

}
