package com.example.Gestion_des_incidents.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

///import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Gestion_des_incidents.Repo.UserRepository;
import com.example.Gestion_des_incidents.Repo.TicketRepository;

import com.example.Gestion_des_incidents.email.EmailBody;
import com.example.Gestion_des_incidents.Entity.Error;

import com.example.Gestion_des_incidents.Entity.StatUser;
import com.example.Gestion_des_incidents.Entity.Tache;
import com.example.Gestion_des_incidents.Entity.Ticket;
import com.example.Gestion_des_incidents.Entity.User;
import com.example.Gestion_des_incidents.Entity.Loger;

import com.example.Gestion_des_incidents.email.EmailServiceImp;


@RestController
@CrossOrigin("*")
public class Controller {

    @Autowired(required = true)
    private UserRepository userR;

    @Autowired(required = true)
    private TicketRepository ticketR;

    @Autowired
    EmailServiceImp sender = new EmailServiceImp();

    @GetMapping(path = "/users")
    ResponseEntity<List<User>> getAllUser() {
        return new ResponseEntity<List<User>>(userR.findAll(), HttpStatus.OK);
    }

    @PostMapping(path = "/save_user")
    ResponseEntity<?> saveUser(@RequestBody User user) {
        Error e = new Error("IMPOSSIBLE", 404);

        for (var user_ : userR.findAll()) {
            if (user_.getEmail() != null) {
                if (user_.getEmail().equals(user.getEmail())) {
                    e.setMessage("EXIST");
                    e.setStatusCode(500);
                    return new ResponseEntity<Error>(e, HttpStatus.OK);
                }
            }
        }

        UUID uuid = UUID.randomUUID();

        user.setToken_confirm(uuid.toString());

        EmailBody emailBody = new EmailBody();

        emailBody.setBody("CREATION DE COMPTE SUR HUB-SERVICE");
        String message = "Salut M/Mme " + user.getName()
                + " Nous vous souhaitons la bienvenue sur notre plateforme HUB-SERVICE\n" +
                "Nous vous envoyons cet mail suite à la création de votre nouveau compte" +
                "\n Veuillez utiliser ce mot de passe afin de pouvoir confirmer la création du compte   code de confirmation: "
                + uuid.toString();
        emailBody.setMessage(message);
        emailBody.setRecipient(user.getEmail());

        String res = sender.sendSimpleMessage(emailBody, user.getEmail());
        if (res.equals("Mail Sent Successfully...")) {
            Error ouput = new Error("CREE", 200);
            userR.save(user);
            return new ResponseEntity<Error>(ouput, HttpStatus.CREATED);
        } else {
            Error e_ = new Error("E-Mail", 500);
            return new ResponseEntity<>(e_, HttpStatus.OK);
        }

    }

    @GetMapping(path = "confirm-compte/{email}/{pwd}")
    ResponseEntity confirmation(@PathVariable(name = "email") String email, @PathVariable(name = "pwd") String pwd) {
        for (var e : userR.findAll()) {
            if (e.getEmail().equals(email) && e.getToken_confirm().equals(pwd)) {
                e.setValide(true);
                e.setToken_confirm("");
                return new ResponseEntity<User>(userR.save(e), HttpStatus.CREATED);

            }

        }
        Error e = new Error("IMPOSSIBLE", 404);
        return new ResponseEntity<Error>(e, HttpStatus.OK);
    }

    // method pour authentification
    @GetMapping(path = "/login/{email}/{password}")
    ResponseEntity<?> loginUser(@PathVariable(name = "email") String email,
                                @PathVariable(name = "password") String password) {
        for (var i : userR.findAll()) {
            if (i.getEmail() != null) {
                if (i.getEmail().equals(email) && i.getPassword().equals(password)) {
                    System.out.println(i.getEmail());
                    if (i.valide == true) {
                        if (i.getRole().equals("TECHNICIEN")) {
                            Loger loger = new Loger(i.getId(), "TECHNICIEN");
                            return new ResponseEntity<Loger>(loger, HttpStatus.OK);
                        } else {
                            Loger loger = new Loger(i.getId(), "USER");
                            return new ResponseEntity<Loger>(loger, HttpStatus.OK);
                        }
                    } else {
                        return new ResponseEntity<>(-2, HttpStatus.OK);
                    }
                }
            }
        }
        return new ResponseEntity<Integer>(-1, HttpStatus.OK);
    }

    @PostMapping(path = "/save_ticket")
    ResponseEntity<Ticket> saveTicket(@RequestBody Ticket ticket) {

        return new ResponseEntity<Ticket>(ticketR.save(ticket), HttpStatus.CREATED);
    }

    @PutMapping(path = "update-ticket/{id}")
    ResponseEntity<Ticket> updateTicket(@PathVariable String id, @RequestBody Ticket ticket) {
        Ticket t = ticketR.save(ticket);

        return new ResponseEntity<Ticket>(t, HttpStatus.CREATED);
    }

    @GetMapping(path = "ticket")
    ResponseEntity<List<Ticket>> allTicket() {
        return new ResponseEntity<List<Ticket>>(ticketR.findAll(), HttpStatus.OK);
    }

    // liste de tous les ticket de l'utilisateur
    /*
     * cette classe retourne la liste de tous les tickets concernant
     * prises en charge par un technicien
     *
     */
    @GetMapping(path = "ticket-technicien/{id}")
    ResponseEntity getTicketForTechnicien(@PathVariable String id) {
        List<Ticket> ticket = new ArrayList<>();
        for (var ti : ticketR.findAll()) {
            if (ti.getTechnicien_id() != null) {
                if (ti.getTechnicien_id().equals(id)) {
                    ticket.add(0, ti);
                }
            } else {
                if (ti.getStatus().equals("Nouveau")) {
                    ticket.add(0, ti);
                }
            }
        }
        return new ResponseEntity<List<Ticket>>(ticket, HttpStatus.OK);
    }

    // liste de tous les ticket de l'utilisateur
    /*
     * cette classe retourne la liste de tous les tickets concernant
     * un utilisateur grâce à son id
     */
    @GetMapping(path = "tickets/{id}")
    ResponseEntity<List<Ticket>> fetchTicketById(@PathVariable String id) {
        List<Ticket> ticket = new ArrayList<Ticket>();
        for (Ticket t : ticketR.findAll()) {
            if (t.getUser_id() != null) {
                if (t.getUser_id().equals(id)) {
                    ticket.add(0, t);
                }
            }
        }
        return new ResponseEntity<List<Ticket>>(ticket, HttpStatus.OK);
    }

    /*
     * Mise à jour de du statut d'un ticket
     */
    @PutMapping(path = "update_state")
    ResponseEntity<Ticket> updateState(@RequestBody Ticket ticket) {
        Ticket t = ticketR.save(ticket);

        return new ResponseEntity<Ticket>(t, HttpStatus.CREATED);
    }

    /*
     * cette méthode rétourne un seul ticket à partir de l'id du ticket
     */
    @GetMapping(path = "single_ticket/{id}")
    ResponseEntity<Optional<Ticket>> getTicketById(@PathVariable String id) {
        return new ResponseEntity<Optional<Ticket>>(ticketR.findById(id), HttpStatus.OK);
    }

    /*
     * retourne juste le nom d'un utilisateur à partir de son id
     */
    @GetMapping(path = "getUserName/{id}")
    ResponseEntity<User> getName(@PathVariable String id) {
        User u = new User();
        String name = userR.findById(id).get().getName();
        u.setName(name);
        System.out.println("\n\n" + name);
        return new ResponseEntity<User>(u, HttpStatus.OK);
    }

    /*
     * Ajout d'une nouvelle tache à un ticket
     */

    @PostMapping(path = "add_taches/{id}")
    ResponseEntity<?> ajouterTache2(@RequestBody Tache tache, @PathVariable String id) {
        Ticket ticket = ticketR.findById(id).get();

        for (var t : ticket.getTache()) {
            if (t.getDescription() != null) {
                if (t.getDescription().equals(tache.getDescription())) {
                    Error e = new Error("EXIST", 500);
                    return new ResponseEntity<Error>(e, null);
                }
            }
        }
        ticket.ajouterTache(tache);
        return new ResponseEntity<Ticket>(ticketR.save(ticket), HttpStatus.OK);

    }

    /*
     * suppression d'une tache à partir de la description
     */
    @PostMapping(path = "remove_tache/{id}")
    ResponseEntity<Ticket> deleteTache(@RequestParam String tache, @PathVariable String id) {
        Ticket ticket = ticketR.findById(id).get();
        for (int i = 0; i < ticket.getTache().size(); i++) {
            if (ticket.getTache().get(i).getDescription().equals(tache)) {
                ticket.getTache().remove(i);
                return new ResponseEntity<Ticket>(ticketR.save(ticket), HttpStatus.OK);

            }
        }
        return new ResponseEntity<Ticket>(HttpStatus.OK);

    }

    // changer l'etat de la tache
    @PostMapping(path = "update_state/{id}")
    ResponseEntity<Ticket> updateState(@RequestParam String tache, @PathVariable String id) {
        Ticket t = ticketR.findById(id).get();
        System.out.println(tache);
        for (int i = 0; i < t.getTache().size(); i++) {
            System.out.println(tache);
            if (t.getTache().get(i).getDescription().equals(tache)) {
                System.out.println(tache);
                t.getTache().get(i).setEtat(!t.getTache().get(i).etat);
                return new ResponseEntity<Ticket>(ticketR.save(t), HttpStatus.OK);

            }
        }
        return new ResponseEntity<Ticket>(ticketR.save(t), HttpStatus.OK);
    }

    /*
     * Ajout d'un technicien par un administrateur
     */
    @PostMapping(path = "add_technicien/{user}/{password}")
    ResponseEntity<?> ajouterTechnicien(@PathVariable(name = "user") String user,
                                        @PathVariable(name = "password") String password,
                                        @RequestBody User u) {
        for (User u_ : userR.findAll()) {
            if (u_.getEmail() != null) {
                if (u_.getEmail().equals(u.getEmail())) {
                    Error e = new Error("Un compte existe déja", 405);
                    return new ResponseEntity<Error>(e, HttpStatus.OK);
                }
            }
        }

        // changer les coordonnées du super administrateur
        if (user.equals("admin") && password.equals("00000000")) {
            u.setRole("TECHNICIEN");
            u.setValide(true);
            return new ResponseEntity<>(userR.save(u), HttpStatus.CREATED);
        }
        Error e = new Error("Donnée administrateur incorrecte", 405);
        return new ResponseEntity<>(e, HttpStatus.OK);
    }

    /*
     * Retourne quelques informations sur un utilisateur ou un technicien
     */
    @GetMapping(path = "info_user/{id}")
    ResponseEntity<User> getInfoUser(@PathVariable String id) {
        User user = userR.findById(id).get();
        User u = new User();
        u.setId(id);
        u.setNumber(user.getNumber());
        u.setName(user.getName());
        u.setEmail(user.getEmail());
        u.setAdresse(user.getAdresse());
        return new ResponseEntity<User>(u, HttpStatus.OK);
    }

    /*
     * Méthode qui retourne quelques statistiques du client
     */
    @GetMapping(path = "get_statistique/{id}")
    ResponseEntity<StatUser> statistique(@PathVariable String id) {
        List<Ticket> ticket = ticketR.findAll();
        int total = 0;
        int pas = 0;
        int encours = 0;
        int terminer = 0;
        for (Ticket t : ticket) {

            if (t.getUser_id() != null) {
                if (t.getUser_id().equals(id)) {
                    total += 1;
                    if (t.getStatus().equals("Nouveau")) {
                        pas += 1;

                    }
                    if (t.getStatus().equals("En cours")) {
                        encours += 1;
                    }
                    if (t.getStatus().equals("Terminer") || t.getStatus().equals("Cloturer")) {
                        terminer += 1;
                    }
                }
            }
        }
        StatUser stat = new StatUser();

        stat.setNombreTerminer(terminer);
        stat.setNombreTotal(total);
        stat.setTicketEnAttente(pas);
        stat.setTicketEncours(encours);
        return new ResponseEntity<StatUser>(stat, HttpStatus.OK);
    }

    @PostMapping(path = "update_user")
    ResponseEntity<User> updateUser(@RequestBody User u) {
        User user = userR.findById(u.getId()).get();
        user.setName(u.getName());
        user.setNumber(u.getNumber());
        user.setAdresse(u.getAdresse());

        return new ResponseEntity<User>(userR.save(user), HttpStatus.OK);
    }

    @PostMapping(path = "send_message")
    Error sendMessage(@RequestBody EmailBody email) {
        System.out.println("Message:\n\n" + email.getMessage());
        // changer le mail pour mettre celui de l'entreprise
        String res = sender.sendSimpleMessage(email, "kh.soufiene09@gmail.com");
        Error e = new Error(res, 200);
        return e;
    }

    @GetMapping(path = "dernier-ticket/{id}")
    List<Ticket> dernierTicket(@PathVariable String id) {
        List<Ticket> output = new ArrayList<Ticket>();
        List<Ticket> t = ticketR.findAll();
        for (var i = t.size() - 1; i >= 0 && output.size() < 3; i--) {
            if (t.get(i).getUser_id().equals(id)) {
                output.add(t.get(i));
            }
        }
        return output;

    }

}


