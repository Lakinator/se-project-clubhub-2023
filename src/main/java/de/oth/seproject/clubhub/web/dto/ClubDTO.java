package de.oth.seproject.clubhub.web.dto;

public record ClubDTO(long id, int userCount, int groupCount, String name, boolean isMember) {
}
