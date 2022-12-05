package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.GroupEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GroupEventRepository extends JpaRepository<GroupEvent, Long> {

    List<GroupEvent> findAllByGroup(Group group);

    List<GroupEvent> findAllByGroupAndEventDateBetweenOrderByEventDateAscEventStartAsc(Group group, LocalDate start, LocalDate end);

}
