package com.example.Gestion_des_incidents.Repo;

import com.example.Gestion_des_incidents.Entity.Technicien;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TechnicienRepository extends MongoRepository<Technicien, String> {

}
