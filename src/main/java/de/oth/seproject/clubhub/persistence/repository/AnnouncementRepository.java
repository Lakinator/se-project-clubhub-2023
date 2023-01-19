package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Announcement;
import de.oth.seproject.clubhub.persistence.model.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * @param club -
     * @return All announcements from a club
     */
    List<Announcement> findAllByClub(Club club);

    /**
     * @param club     -
     * @param pageable -
     * @return A page with announcements from a club
     */
    Page<Announcement> findAllByClub(Club club, Pageable pageable);

}
