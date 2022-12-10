package de.oth.seproject.clubhub.rest.v1.api.dto;

import de.oth.seproject.clubhub.persistence.model.EventType;
import de.oth.seproject.clubhub.persistence.model.GroupEvent;

import java.time.LocalDate;
import java.time.LocalTime;

public record GroupEventDTO(LocalDate eventDate, LocalTime eventStart, LocalTime eventEnd,
                            String location,
                            String title, String description, EventType eventType) {

    public static GroupEventDTO of(GroupEvent groupEvent) {
        return new GroupEventDTO(groupEvent.getEventDate(), groupEvent.getEventStart(), groupEvent.getEventEnd(),
                groupEvent.getLocation().toString(), groupEvent.getTitle(), groupEvent.getDescription(), groupEvent.getEventType());
    }

}
