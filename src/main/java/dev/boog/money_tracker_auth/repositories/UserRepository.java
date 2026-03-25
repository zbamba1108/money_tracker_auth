package dev.boog.money_tracker_auth.repositories;

import dev.boog.money_tracker_auth.entities.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(@Email String email);

}
