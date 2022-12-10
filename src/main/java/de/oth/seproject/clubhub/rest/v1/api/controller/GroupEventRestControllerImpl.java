package de.oth.seproject.clubhub.rest.v1.api.controller;

import de.oth.seproject.clubhub.rest.v1.api.dto.GroupEventDTO;
import de.oth.seproject.clubhub.rest.v1.api.service.GroupEventRestService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public non-sealed class GroupEventRestControllerImpl implements GroupEventRestController {

    private final GroupEventRestService groupEventRestService;

    public GroupEventRestControllerImpl(GroupEventRestService groupEventRestService) {
        this.groupEventRestService = groupEventRestService;
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping("/club/{clubName}/group/{groupName}/events")
    @Override
    public List<GroupEventDTO> getAllEventsBetween(@PathVariable String clubName, @PathVariable String groupName,
                                                   @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                   @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End can't be before start!");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start can't be in the past!");
        }

        return groupEventRestService.getAllEventsBetween(clubName, groupName, startDate, endDate).stream().map(GroupEventDTO::of).toList();
    }
}
