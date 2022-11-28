package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findAllByClub(Club club);

    Page<Group> findAllByClub(Club club, Pageable pageable);

}