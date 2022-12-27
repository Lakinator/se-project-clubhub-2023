package de.oth.seproject.clubhub.rest.v1.api.dto;

import java.time.LocalDateTime;
import de.oth.seproject.clubhub.persistence.model.User;

public record UserDTO(LocalDateTime time, String firstName, String lastName, String clubName) {

    public static UserDTO of(User user) {
        return new UserDTO(LocalDateTime.now(), user.getFirstName(), user.getLastName(), user.getClub().getName());
    }

}
