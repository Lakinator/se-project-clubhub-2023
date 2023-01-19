package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * @param email -
     * @return A user with an email
     */
    Optional<User> findByEmail(String email);

    /**
     * @param firstName -
     * @param lastName  -
     * @return A user with a first name and a last name
     */
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);
    
    /**
     * @param club -
     * @return All users in a club
     */
    List<User> findAllByClub(Club club);

    /**
     * @param email -
     * @return Whether a user with an email exists
     */
    Boolean existsByEmail(String email);

}
