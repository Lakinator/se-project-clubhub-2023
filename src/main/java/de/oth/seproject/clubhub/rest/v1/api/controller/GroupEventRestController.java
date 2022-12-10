package de.oth.seproject.clubhub.rest.v1.api.controller;

import de.oth.seproject.clubhub.rest.v1.api.dto.GroupEventDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public sealed interface GroupEventRestController permits GroupEventRestControllerImpl {

    /**
     * Get all events of a group in a specific club which are set between the given start and end.
     *
     * @param clubName Name of the club
     * @param groupName Name of the group in the club
     * @param startDate Start
     * @param endDate End
     * @return List of all events between the given start and end
     */
    List<GroupEventDTO> getAllEventsBetween(@PathVariable String clubName, @PathVariable String groupName,
                                            @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                            @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

}
