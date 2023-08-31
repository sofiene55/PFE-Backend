package com.example.Gestion_des_incidents.Repo;

import java.util.List;
import com.example.Gestion_des_incidents.Entity.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
public interface TicketRepository extends MongoRepository<Ticket, String> {
    @Query("SELECT * from ticket ORDER BY ASK date")
    List<Ticket> findAllTicketsReverse() throws Exception;
}
