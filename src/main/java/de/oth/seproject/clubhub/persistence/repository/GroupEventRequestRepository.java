package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.GroupEventRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GroupEventRequestRepository extends JpaRepository<GroupEventRequest, Long> {

    /**
     * @param group -
     * @return All group event requests which were created by the given group
     */
    Page<GroupEventRequest> findAllByCreatorGroup(Group group, Pageable pageable);

    /**
     * @param group -
     * @return All group event requests which were received by the given group
     */
    Page<GroupEventRequest> findAllByRequestedGroup(Group group, Pageable pageable);

    /**
     * @param club  -
     * @param start -
     * @param end   -
     * @return All group event requests which were created by the given club in the given timespan
     */
    List<GroupEventRequest> findAllByCreatorGroup_ClubAndEventDateBetweenOrderByEventDateAscEventStartAsc(Club club, LocalDate start, LocalDate end);

    /**
     * @param club  -
     * @param start -
     * @param end   -
     * @return All group event requests which were received by the given club in the given timespan
     */
    List<GroupEventRequest> findAllByRequestedGroup_ClubAndEventDateBetweenOrderByEventDateAscEventStartAsc(Club club, LocalDate start, LocalDate end);

}
