package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.Announcement;
import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    /**
     * @param group -
     * @return All surveys for a group
     */
    List<Survey> findAllByGroup(Group group);

    /**
     * @param group    -
     * @param pageable -
     * @return A page with surveys for a group
     */
    Page<Survey> findAllByGroup(Group group, Pageable pageable);
}
