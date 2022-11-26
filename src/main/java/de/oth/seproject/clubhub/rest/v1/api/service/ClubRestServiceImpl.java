package de.oth.seproject.clubhub.rest.v1.api.service;

import de.oth.seproject.clubhub.persistence.model.Club;
import de.oth.seproject.clubhub.persistence.repository.ClubRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public non-sealed class ClubRestServiceImpl implements ClubRestService {

    private final ClubRepository clubRepository;

    public ClubRestServiceImpl(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }
}
