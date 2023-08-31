package com.example.Gestion_des_incidents.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatUser {
    int nombreTotal = 0;
    int nombreTerminer = 0;
    int ticketEncours = 0;
    int ticketEnAttente = 0;
}
