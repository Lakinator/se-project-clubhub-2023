package de.oth.seproject.clubhub.rest.v1.api.dto;

import de.oth.seproject.clubhub.persistence.model.Club;

import java.time.LocalDateTime;

/**
 * @param timeStamp Timestamp when the entity was retrieved
 * @param name      Name of the club
 */
public record ClubDTO(LocalDateTime timeStamp, String name, int memberCount) {

    public static ClubDTO of(Club club) {
        return new ClubDTO(LocalDateTime.now(), club.getName(), club.getUsers().size());
    }

}
