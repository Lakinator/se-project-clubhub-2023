package de.oth.seproject.clubhub.rest.v1.api.service;

import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.GroupEvent;
import de.oth.seproject.clubhub.persistence.repository.GroupEventRepository;
import de.oth.seproject.clubhub.persistence.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public non-sealed class GroupEventRestServiceImpl implements GroupEventRestService {

    private final GroupRepository groupRepository;

    private final GroupEventRepository groupEventRepository;

    public GroupEventRestServiceImpl(GroupRepository groupRepository, GroupEventRepository groupEventRepository) {
        this.groupRepository = groupRepository;
        this.groupEventRepository = groupEventRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GroupEvent> getAllEventsBetween(String clubName, String groupName, LocalDate startDate, LocalDate endDate) {
        Optional<Group> optionalGroup = groupRepository.findByClubNameAndName(clubName, groupName);

        List<GroupEvent> eventList = new ArrayList<>();

        optionalGroup.ifPresent(group -> {
            eventList.addAll(groupEventRepository.findAllByGroupAndEventDateBetweenOrderByEventDateAscEventStartAsc(group, startDate, endDate));
        });

        return eventList;
    }
}
