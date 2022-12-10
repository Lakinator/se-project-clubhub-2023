package de.oth.seproject.clubhub.rest.v1.api.service;

import de.oth.seproject.clubhub.persistence.model.GroupEvent;

import java.time.LocalDate;
import java.util.List;

public sealed interface GroupEventRestService permits GroupEventRestServiceImpl {

    /**
     * Get all events of a group in a specific club which are set between the given start and end.
     *
     * @param clubName Name of the club
     * @param groupName Name of the group in the club
     * @param startDate Start
     * @param endDate End
     * @return List of all events between the given start and end
     */
    List<GroupEvent> getAllEventsBetween(String clubName, String groupName, LocalDate startDate, LocalDate endDate);

}
