package de.oth.seproject.clubhub.web.dto;

import de.oth.seproject.clubhub.persistence.model.GroupEventRequest;

public record GroupEventRequestDTO(GroupEventRequest groupEventRequest, boolean isTrainerInCreatorGroup, boolean isTrainerInRequestedGroup) {
}
