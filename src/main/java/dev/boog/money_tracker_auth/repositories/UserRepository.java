package dev.boog.money_tracker_auth.repositories;

import dev.boog.money_tracker_auth.entities.*;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(@Email String email);

}
