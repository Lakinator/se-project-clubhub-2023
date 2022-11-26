package de.oth.seproject.clubhub.rest.v1.api.controller;

import de.oth.seproject.clubhub.rest.v1.api.dto.ClubDTO;

import java.util.List;

public sealed interface ClubRestController permits ClubRestControllerImpl {

    /**
     * @return A list of all available clubs
     */
    List<ClubDTO> getAllClubs();

}
