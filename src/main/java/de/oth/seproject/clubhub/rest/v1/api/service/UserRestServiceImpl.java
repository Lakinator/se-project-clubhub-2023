package de.oth.seproject.clubhub.rest.v1.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.model.Group;
import de.oth.seproject.clubhub.persistence.model.Role;
import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.repository.ClubRepository;
import de.oth.seproject.clubhub.persistence.repository.GroupRepository;
import de.oth.seproject.clubhub.persistence.repository.RoleRepository;
import de.oth.seproject.clubhub.persistence.repository.UserRepository;

@Service
public non-sealed class UserRestServiceImpl implements UserRestService {

    private final UserRepository userRepository;

    private final ClubRepository clubRepository;

    private final GroupRepository groupRepository;

    private final RoleRepository roleRepository;

    public UserRestServiceImpl(UserRepository userRepository, ClubRepository clubRepository, GroupRepository groupRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;		
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getAllUsersInClub(String name) {
        Optional<Club> optionalClub = clubRepository.findByName(name);

        List<User> userList = new ArrayList<>();

        optionalClub.ifPresent(club -> {
            userList.addAll(userRepository.findAllByClub(club));
        });

        return userList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getAllUsersInClubInGroup(String clubName, String groupName) {
        Optional<Group> optionalGroup = groupRepository.findByClubNameAndName(clubName, groupName);

        List<User> userList = new ArrayList<>();

        optionalGroup.ifPresent(group -> {
            List<Role> roles = roleRepository.findAllByGroup(group);
            for (Role role : roles) {
                userList.add(role.getUser());
            }
        });

        return userList;
    }
}