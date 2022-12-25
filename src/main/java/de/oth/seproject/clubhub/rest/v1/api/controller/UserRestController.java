package de.oth.seproject.clubhub.rest.v1.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import de.oth.seproject.clubhub.rest.v1.api.dto.UserDTO;

public sealed interface UserRestController permits UserRestControllerImpl {

    /**
     * @return A list of all users
     */
	List<UserDTO> getAllUsers();
    
    /**
     * @return A list of all users in a club
     */
    List<UserDTO> getAllUsersInClub(@PathVariable String clubName);
    
    /**
     * @return A list of all users in a club from a group
     */
    List<UserDTO> getAllUsersInClubInGroup(@PathVariable String clubName, @PathVariable String groupName);

}
