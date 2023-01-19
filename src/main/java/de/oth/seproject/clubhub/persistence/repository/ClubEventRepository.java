package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.model.ClubEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubEventRepository extends JpaRepository<ClubEvent, Long> {

    /**
     * @param club -
     * @return All club events from a club
     */
    List<ClubEvent> findAllByClub(Club club);

}
