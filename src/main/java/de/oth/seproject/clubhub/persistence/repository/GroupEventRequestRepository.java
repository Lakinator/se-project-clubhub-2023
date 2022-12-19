package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.GroupEventRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupEventRequestRepository extends JpaRepository<GroupEventRequest, Long> {

    /**
     * @param group -
     * @return All group event requests which were created by the given group
     */
    List<GroupEventRequest> findAllByCreatorGroup(Group group);

    /**
     * @param group -
     * @return All group event requests which were received by the given group
     */
    List<GroupEventRequest> findAllByRequestedGroup(Group group);

}
