package de.oth.seproject.clubhub.rest.v1.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.oth.seproject.clubhub.rest.v1.api.dto.ClubDTO;
import de.oth.seproject.clubhub.rest.v1.api.dto.UserDTO;
import de.oth.seproject.clubhub.rest.v1.api.service.UserRestService;

@RestController
@RequestMapping("/api/v1/users")
public non-sealed class UserRestControllerImpl implements UserRestController{

    private final UserRestService userRestService;
    
    public UserRestControllerImpl(UserRestService userRestService) {
    	this.userRestService = userRestService;
	}

    /**
     * {@inheritDoc}
     */
    @GetMapping("/all")
    @Override
	public List<UserDTO> getAllUsers() {
		return userRestService.getAllUsers().stream().map(UserDTO::of).toList();
	}
    
    /**
     * {@inheritDoc}
     */
    @GetMapping("/club/{clubName}")
    @Override
	public List<UserDTO> getAllUsersInClub(@PathVariable String clubName) {
		return userRestService.getAllUsersInClub(clubName).stream().map(UserDTO::of).toList();
	}
    
    /**
     * {@inheritDoc}
     */
    @GetMapping("/club/{clubName}/group/{groupName}")
    @Override
	public List<UserDTO> getAllUsersInClubInGroup(@PathVariable String clubName, @PathVariable String groupName) {
		return null;
	}

}
