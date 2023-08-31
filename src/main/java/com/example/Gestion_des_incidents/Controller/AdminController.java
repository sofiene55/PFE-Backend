package com.example.Gestion_des_incidents.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.Gestion_des_incidents.Entity.Error;
import com.example.Gestion_des_incidents.Entity.Ticket;
import com.example.Gestion_des_incidents.Entity.User;
import com.example.Gestion_des_incidents.Repo.TicketRepository;
import com.example.Gestion_des_incidents.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin/")
@CrossOrigin("*")
public class AdminController {

    @Autowired(required = true)
    UserRepository userR;

    @Autowired(required = true)
    TicketRepository ticketR;

    String password = "12345678";
    String adminname = "administrateur";

    @PostMapping(path = "add_technicien/{password}")
    ResponseEntity<?> ajouterTechnicien(
            @PathVariable(name = "password") String password,
            @RequestBody User u) {
        for (User u_ : userR.findAll()) {
            if (u_.getEmail() != null) {
                if (u_.getEmail().equals(u.getEmail()) && u_.getRole().equals("TECHNICIEN")) {
                    Error e = new Error("Un compte existe déja", 405);
                    return new ResponseEntity<Error>(e, HttpStatus.OK);
                }
            }
        }

        // changer les coordonnées du super administrateur
        if (password.equals(this.password)) {
            u.setRole("TECHNICIEN");
            u.setValide(true);
            return new ResponseEntity<>(userR.save(u), HttpStatus.CREATED);
        }
        Error e = new Error("Donnée administrateur incorrecte",
                405);
        return new ResponseEntity<>(e, HttpStatus.OK);
    }

    @GetMapping(path = "all_technicien/{choice}")
    ResponseEntity<List<User>> getTechnicien(@PathVariable String choice) {
        List<User> users = new ArrayList<User>();
        switch (choice) {
            case "TECHNICIEN": {

                for (var i : userR.findAll()) {
                    if (i.getRole().toUpperCase().equals("TECHNICIEN")) {
                        users.add(i);
                    }
                }
                return new ResponseEntity<List<User>>(users, HttpStatus.OK);

            }
            case "USER": {
                for (var i : userR.findAll()) {
                    if (i.getRole().toUpperCase().equals("USER")) {
                        users.add(i);
                    }
                }
                return new ResponseEntity<List<User>>(users, HttpStatus.OK);
            }
            default:
                return null;
        }
    }

    @PostMapping(path = "login")
    ResponseEntity<Error> loginAdmin(@RequestBody User login) {
        // System.out.println("Affiché" + UUID.+);
        if (login.getEmail().equals(adminname) && login.getPassword().equals(this.password)) {

            Error e = new Error(login.getPassword(), 0);
            return new ResponseEntity<Error>(e, HttpStatus.OK);
        }

        Error e = new Error("NOT FOUND", 0);
        return new ResponseEntity<Error>(e, HttpStatus.OK);

    }

    @DeleteMapping(path = "deleteT/{id}/{admin}/{type}")
    ResponseEntity<?> deleteTechnicien(@PathVariable(name = "id") String id,
            @PathVariable(name = "admin") String user, @PathVariable(name = "type") String choice) {
        if (user.equals(this.password)) {
            if (choice.equals("TECHNICIEN")) {
                userR.deleteById(id);
            } else {
                if (choice.equals("TICKET")) {
                    ticketR.deleteById(id);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<Error>(new Error("ERREUR SUPPRESSION", 0), HttpStatus.OK);
    }

    @GetMapping(path = "/all-tickets/{id}")
    ResponseEntity<List<Ticket>> getAllTicket(@PathVariable(name = "id") String password_) {
        if (password_.equals(this.password)) {
            return new ResponseEntity<List<Ticket>>(ticketR.findAll(), HttpStatus.OK);
        }
        return null;
    }

    @GetMapping(path = "/stats/{id}")
    ResponseEntity<Map<String, Integer>> getStat(@PathVariable String id) {
        Map<String, Integer> output = new HashMap<>();
        if (id.equals(password)) {
            int nbT = 0;
            int nbU = 0;
            int nbTicketT = 0;
            int nbTicketC = 0;
            int nbTicketCl = 0;
            int nbTicketN = 0;
            int nbTT = 0;
            for (var i : userR.findAll()) {
                if (i.getRole().equals("TECHNICIEN")) {
                    nbT += 1;
                }
                if (i.getRole().equals("USER")) {
                    nbU += 1;
                }
            }
            for (var j : ticketR.findAll()) {
                if (j.getStatus().equals("Nouveau")) {
                    nbTicketN += 1;
                }
                if (j.getStatus().equals("Cloturer")) {
                    nbTicketCl += 1;
                }
                if (j.getStatus().equals("Terminer")) {
                    nbTicketT += 1;
                }
                if (j.getStatus().equals("En cours")) {
                    nbTicketC += 1;
                }
                nbTT += 1;
            }
            output.put("nombre technicien", nbT);
            output.put("nombre user", nbU);
            output.put("terminer", nbTicketT);
            output.put("cloturer", nbTicketCl);
            output.put("en cours", nbTicketC);
            output.put("nouveau", nbTicketN);
            output.put("total ticket", nbTT);
            return new ResponseEntity<Map<String, Integer>>(output, HttpStatus.OK);
        }
        return null;
    }

    @GetMapping(path = "details-technicien/{id}/{password}/{choice}")
    ResponseEntity<Map<String, String>> getDetailsTechnicien(@PathVariable String id, @PathVariable String password,
            @PathVariable String choice) {
        if (password.equals(this.password)) {
            User u = userR.findById(id).get();
            Map<String, String> output = new HashMap<>();
            output.put("name", u.getName());
            output.put("addresse", u.getAdresse());
            output.put("email", u.getEmail());
            output.put("telephone", u.getNumber());
            int nbE = 0;
            int nbT = 0;
            int nbC = 0;
            int nbTotal = 0;
            int nbNouveau = 0;
            switch (choice) {
                case "TECHNICIEN": {
                    for (var i : ticketR.findAll()) {
                        if (i.getTechnicien_id() != null) {
                            if (i.getTechnicien_id().equals(id)) {
                                if (i.getStatus().equals("En cours")) {
                                    nbE += 1;
                                }
                                if (i.getStatus().equals("Terminer")) {
                                    nbT += 1;
                                }
                                if (i.getStatus().equals("Cloturer")) {
                                    nbC += 1;
                                }
                                if (i.getStatus().equals("Nouveau")) {
                                    nbNouveau += 1;
                                }
                                nbTotal += 1;
                            }
                        }
                    }
                    output.put("terminer", String.valueOf(nbT));
                    output.put("en cours", String.valueOf(nbE));
                    output.put("cloturer", String.valueOf(nbC));
                    output.put("cree", String.valueOf(nbTotal));
                    output.put("nouveau", String.valueOf(nbNouveau));
                    break;
                }
                case "USER": {
                    for (var i : ticketR.findAll()) {
                        if (i.getUser_id() != null) {

                            if (i.getUser_id().equals(id)) {
                                if (i.getStatus().equals("En cours")) {
                                    nbE += 1;
                                }
                                if (i.getStatus().equals("Terminer")) {
                                    nbT += 1;
                                }
                                if (i.getStatus().equals("Cloturer")) {
                                    nbC += 1;
                                }
                                if (i.getStatus().equals("Nouveau")) {
                                    nbNouveau += 1;
                                }
                                nbTotal += 1;
                            }
                        }
                    }
                    output.put("terminer", String.valueOf(nbT));
                    output.put("en cours", String.valueOf(nbE));
                    output.put("cloturer", String.valueOf(nbC));
                    output.put("cree", String.valueOf(nbTotal));
                    output.put("nouveau", String.valueOf(nbNouveau));
                    break;
                }
            }
            return new ResponseEntity<Map<String, String>>(output, HttpStatus.OK);
        }
        return null;
    }

}
