package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
	
    /**
     * @param name -
     * @return A club with a name
     */
    Optional<Club> findByName(String name);

    /**
     * @param pageable -
     * @return A page with clubs
     */
    Page<Club> findAll(Pageable pageable);
}
