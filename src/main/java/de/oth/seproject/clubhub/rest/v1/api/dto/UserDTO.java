package de.oth.seproject.clubhub.rest.v1.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import de.oth.seproject.clubhub.persistence.model.User;
import de.oth.seproject.clubhub.persistence.model.Role;

public record UserDTO(LocalDateTime time, String firstName, String lastName, String clubName) {
	
	public static UserDTO of(User user) {
		return new UserDTO(LocalDateTime.now(), user.getFirstName(), user.getLastName(), user.getClub().getName());
	}

}
