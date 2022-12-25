package de.oth.seproject.clubhub.rest.v1.api.service;

import java.util.List;

import de.oth.seproject.clubhub.persistence.model.User;

public sealed interface UserRestService permits UserRestServiceImpl {

    /**
     * Get all users.
     * 
     * @return A list of all users
     */
    List<User> getAllUsers();
    
    /**
     * Get all users in a specific club.
     *
     * @param name Name of the club
     * @return List of all users in a club
     */
    List<User> getAllUsersInClub(String name);
    
    /**
     * Get all users of a group in a specific club.
     *
     * @param clubName Name of the club
     * @param groupName Name of the group in the club
     * @return List of all users in a club from a group
     */
    List<User> getAllUsersInClubInGroup(String clubName, String groupName);

}