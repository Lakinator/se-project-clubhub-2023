package de.oth.seproject.clubhub.web.dto;

public record GroupDTO(long id, int userCount, String name, boolean hasJoined, boolean isTrainer) {
}