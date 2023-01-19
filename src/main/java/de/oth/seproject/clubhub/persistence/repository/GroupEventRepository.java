package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.GroupEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GroupEventRepository extends JpaRepository<GroupEvent, Long> {

    /**
     * @param group -
     * @return All group events from a group
     */
    List<GroupEvent> findAllByGroup(Group group);

    /**
     * @param group -
     * @param start -
     * @param end   -
     * @return Group events from a group between start and end
     */
    List<GroupEvent> findAllByGroupAndEventDateBetweenOrderByEventDateAscEventStartAsc(Group group, LocalDate start, LocalDate end);

}
