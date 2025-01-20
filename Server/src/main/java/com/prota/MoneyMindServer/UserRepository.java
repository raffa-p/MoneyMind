package com.prota.MoneyMindServer.DBrepository;

import com.prota.MoneyMindServer.DBentity.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Prota Raffaele
 */

public interface UserRepository extends CrudRepository<User, String> {

     /**
     * Finds a user by username.
     *
     * @param username the username to search for.
     * @return an Optional containing the user if found, or empty if not.
     */
    Optional<User> findByUsername(String username);
}
