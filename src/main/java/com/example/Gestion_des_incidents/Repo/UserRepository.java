package com.example.Gestion_des_incidents.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.Gestion_des_incidents.Entity.User;
public interface UserRepository extends MongoRepository<User, String> {

}
