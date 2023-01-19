package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * @param club -
     * @return All groups from a club
     */
    List<Group> findAllByClub(Club club);

    /**
     * @param club     -
     * @param pageable -
     * @return A page with groups from a club
     */
    Page<Group> findAllByClub(Club club, Pageable pageable);

    /**
     * @param clubName -
     * @param name     -
     * @return A group from a club with a name
     */
    Optional<Group> findByClubNameAndName(String clubName, String name);

}
