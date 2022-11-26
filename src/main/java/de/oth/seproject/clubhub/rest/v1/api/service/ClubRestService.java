package de.oth.seproject.clubhub.rest.v1.api.service;

import de.oth.seproject.clubhub.persistence.model.Club;

import java.util.List;

public sealed interface ClubRestService permits ClubRestServiceImpl {

    /**
     * @return A list of all available clubs
     */
    List<Club> getAllClubs();

}
