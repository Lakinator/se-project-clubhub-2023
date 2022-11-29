package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.GroupEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupEventRepository extends JpaRepository<GroupEvent, Long> {

    List<GroupEvent> findAllByGroup(Group group);

}
